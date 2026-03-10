import os

import chromadb
from openai import OpenAI


os.makedirs("./chroma-db", exist_ok=True)

#client = OpenAI(api_key="replace with working API key")

chroma_client = chromadb.PersistentClient(path="./chroma-db")

collection = chroma_client.get_or_create_collection(
    name="usca"
)

def get_embedding(text):
    response = client.embeddings.create(
        model="text-embedding-3-small",
        input=text
    )
    return response.data[0].embedding

chunks_dir = "chunked-dir"

all_chunks = []

for file in os.listdir(chunks_dir):
    if not file.endswith(".txt"):
        continue

    filepath = os.path.join(chunks_dir, file)

    with open(filepath, "r", encoding="utf-8", errors="replace") as f:
        text = f.read()

    all_chunks.append({
        "id": file.replace(".txt", ""),
        "text": text,
        "metadata": {
            "source_file": file
        }
    })


batch_size = 50

for i in range(0, len(all_chunks), batch_size):

    batch = all_chunks[i:i + batch_size]

    ids = []
    docs = []
    embeddings = []
    metadatas = []

    for item in batch:
        ids.append(item["id"])
        docs.append(item["text"])
        embeddings.append(get_embedding(item["text"]))
        metadatas.append(item["metadata"])

    collection.add(
        ids=ids,
        documents=docs,
        embeddings=embeddings,
        metadatas=metadatas
    )

    print(f"Inserted batch {i // batch_size + 1}")

chroma_client.persist()