import pandas as pd
import string

INPUT_FILE = "160kPosAndNegNoFeatures.csv"
OUTPUT_FILE = "160k_WithEPSB.csv"


def epsb_features(pw: str):
    capitals = sum(1 for c in pw if c.isupper())
    lowercase = sum(1 for c in pw if c.islower())
    letters = capitals + lowercase
    numbers = sum(1 for c in pw if c.isdigit())
    symbols = len(pw) - letters - numbers
    length = len(pw)

    return [capitals, lowercase, letters, numbers, symbols, length]


df = pd.read_csv(INPUT_FILE)


output_rows = []

for _, row in df.iterrows():
    pw1, pw2, label = row['Password1'], row['Password2'], row['Label']

    pw1_feats = epsb_features(str(pw1))
    pw2_feats = epsb_features(str(pw2))

    combined = [pw1, pw2, label] + pw1_feats + pw2_feats
    output_rows.append(combined)

columns = [
    "Password1", "Password2", "Label",
    "cap1", "low1", "letters1", "numbers1", "symbols1", "length1",
    "cap2", "low2", "letters2", "numbers2", "symbols2", "length2"
]

out_df = pd.DataFrame(output_rows, columns=columns)
out_df.to_csv(OUTPUT_FILE, index=False)

print(f"Saved EPSB feature file → {OUTPUT_FILE}")
print(out_df.head())
