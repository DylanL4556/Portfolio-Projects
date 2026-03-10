import pandas as pd

df = pd.read_csv("160k_WithEPSB.csv")

def jaccard_similarity(p1, p2):
    if not p1 or not p2:
        return 0.0
    s1, s2 = set(str(p1)), set(str(p2))
    return len(s1 & s2) / len(s1 | s2)

def levenshtein_distance(s1, s2):
    if not s1:
        return len(str(s2))
    if not s2:
        return len(str(s1))

    prev = list(range(len(str(s2)) + 1))
    for i, c1 in enumerate(str(s1)):
        curr = [i + 1]
        for j, c2 in enumerate(str(s2)):
            curr.append(min(
                prev[j + 1] + 1,
                curr[j] + 1,
                prev[j] + (c1 != c2)
            ))
        prev = curr
    return prev[-1]

def shared_characters(p1, p2):
    if not p1 or not p2:
        return 0
    return len(set(str(p1)) & set(str(p2)))

# Add new columns
df["jaccard_similarity"] = df.apply(
    lambda r: jaccard_similarity(r["Password1"], r["Password2"]), axis=1
)

df["levenshtein_distance"] = df.apply(
    lambda r: levenshtein_distance(r["Password1"], r["Password2"]), axis=1
)

df["shared_characters"] = df.apply(
    lambda r: shared_characters(r["Password1"], r["Password2"]), axis=1
)

df["length_difference"] = (df["length1"] - df["length2"]).abs()

df.to_csv("zEPSB&Enriched", index=False)
