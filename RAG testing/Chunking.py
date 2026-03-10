import re

import tiktoken
import os
import unicodedata

enc = tiktoken.get_encoding("cl100k_base")

def chunk_text_tokens(text, chunk_size=500, overlap=100):
    tokens = enc.encode(text)
    chunks = []
    start = 0

    while start < len(tokens):
        end = start + chunk_size
        chunk_tokens = tokens[start:end]
        chunk_text = enc.decode(chunk_tokens)
        chunks.append(chunk_text)
        start += chunk_size - overlap

    return chunks

def parse_document(text):
    lines = text.splitlines()
    url = lines[0].replace("url:", "").strip()
    title = lines[1].replace("title:", "").strip()
    body = "\n".join(lines[2:]).strip()
    return url, title, body

def safe_filename(text):
    text = text.lower().strip()
    text = re.sub(r"[^a-z0-9]+", "_", text)
    text = text.strip("_")
    return text[:80]

def save_chunk(chunk, title, index, out_dir="chunks"):
    os.makedirs(out_dir, exist_ok=True)

    base = safe_filename(title)
    filename = f"{base}_chunk_{index+1}.txt"

    with open(os.path.join(out_dir, filename), "w", encoding="utf-8") as f:
        f.write(chunk)
    return filename

def clean_text(text):
    text = unicodedata.normalize("NFKC", text)
    return text

def read_file_safely(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            return f.read()
    except UnicodeDecodeError:
        with open(path, "r", encoding="cp1252") as f:
            return f.read()

dirr = "website-content2"
chunked_dir = "chunked-dir"
chunked_docs = []

for file in os.listdir(dirr):
    if not file.endswith(".txt"):
        continue

    raw = read_file_safely(os.path.join(dirr, file))
    raw = clean_text(raw)
    url, title, body = parse_document(raw)
    chunks = chunk_text_tokens(body)

    for i, chunk in enumerate(chunks):

        if len(chunk.strip()) < 100:
            continue

        chunked_docs.append({
            "id": f"{file}_chunk_{i+1}",
            "text": chunk,
            "metadata": {
                "url": url,
                "title": title,
                "chunk_index": i,
                "source_file": file
            }
        })

        save_chunk(chunk, title, i, out_dir=chunked_dir)

print(f"Generated {len(chunked_docs)} chunks")
