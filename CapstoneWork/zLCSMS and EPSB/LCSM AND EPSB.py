import csv
import string


def get_composition_features(pw):
    caps = sum(1 for c in pw if c.isupper())
    low = sum(1 for c in pw if c.islower())
    letters = sum(1 for c in pw if c.isalpha())
    numbers = sum(1 for c in pw if c.isdigit())
    symbols = sum(1 for c in pw if c in string.punctuation)
    return caps, low, letters, numbers, symbols


def add_features(input_csv, output_csv):
    with open(input_csv, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        rows = list(reader)

    updated_rows = []

    for row in rows:
        pw1, pw2 = row["Password1"], row["Password2"]


        caps1, low1, letters1, numbers1, symbols1 = get_composition_features(str(pw1))
        caps2, low2, letters2, numbers2, symbols2 = get_composition_features(str(pw2))


        row.update({
            "caps1": caps1, "low1": low1, "letters1": letters1, "numbers1": numbers1, "symbols1": symbols1,
            "caps2": caps2, "low2": low2, "letters2": letters2, "numbers2": numbers2, "symbols2": symbols2
        })
        updated_rows.append(row)


    new_columns = (
        ["Password1","Password2",
         "Len1","Len2","LenDiff",
         "LCS_1","LCS_2","LCS_3",
         "LCS_Ratio_1","LCS_Ratio_2","LCS_Ratio_3",
         "Levenshtein","Shared_Chars","Jaccard_Sim",
         "caps1","low1","letters1","numbers1","symbols1",
         "caps2","low2","letters2","numbers2","symbols2",
         "Label"]
    )

    with open(output_csv, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=new_columns)
        writer.writeheader()
        writer.writerows(updated_rows)

    print(f"Feature columns added. Saved to {output_csv}")


if __name__ == "__main__":
    add_features("160KLCSMS_FeaturedSet.csv", "Updated_FeaturedSet.csv")
