import pandas as pd

INPUT_FILE = "FeaturedSet_NegAndPosGoodRatio.csv"
OUTPUT_FILE = "FeaturedSet_NegAndPosGoodRatio.csv"

df = pd.read_csv(INPUT_FILE)
#Len1,Len2,LenDiff,Levenshtein,Shared_Chars,Jaccard_Sim

df = df.drop(columns=["Len1",'Len2','LenDiff','Levenshtein','Shared_Chars','Jaccard_Sim'])


df.to_csv(OUTPUT_FILE, index=False)

print(f"Saved cleaned file as {OUTPUT_FILE}")
print(df.head())
