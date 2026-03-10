from openai import OpenAI
import os
import chromadb

client = OpenAI(api_key="replace with copy of your api key if you want this to work.")

chroma_client = chromadb.PersistentClient(path="./chroma-db")

collection = chroma_client.get_or_create_collection(
    name="usca"
)
# test
print("Total documents in collection:", len(collection.get()["ids"]))

def get_embedding(text):
    response = client.embeddings.create(
        model="text-embedding-3-small",
        input=text
    )
    return response.data[0].embedding


#conv query sequence to func later
query = input("Enter prompt: ")

query_embedding = get_embedding(query)

results = collection.query(
    query_embeddings=[query_embedding],
    n_results=50  #number of chunks to retrieve, increased from 5 -> 50 to find additional relevant information
)

retrieved = []
for doc, meta in zip(results["documents"][0], results["metadatas"][0]):
    print("Title:", meta.get("title", ""))
    print("URL:", meta.get("url", ""))
    print("Text snippet:", doc[:300], "\n---")
    retrieved.append(doc)

context = "\n\n".join(retrieved)
context = context[:12000] # prevent overloaded msg, gpt char limit

response = client.responses.create(
    model="gpt-5.2",
    input=[
        {
            "role": "system",
            "content": "Answer ONLY using the provided context. If the answer is not in the context, say you don't know."
        },
        {
            "role": "user",
            "content": f"""
        Context:
        {context}

        Question:
        {query}
        """
               }
    ]
)

answer = response.output_text
print(answer)
