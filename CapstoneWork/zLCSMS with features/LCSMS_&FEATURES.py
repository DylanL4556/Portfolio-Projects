import csv
import math
from Levenshtein import distance as levenshtein_distance


def longest_common_substring(s1, s2):
    longest = ""
    len1, len2 = len(s1), len(s2)
    dp = [[""] * (len2 + 1) for _ in range(len1 + 1)]

    for i in range(1, len1 + 1):
        for j in range(1, len2 + 1):
            if s1[i - 1] == s2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + s1[i - 1]
                if len(dp[i][j]) > len(longest):
                    longest = dp[i][j]

    return longest if len(longest) >= 2 else ""



def lcs_three_parts(p1, p2):
    s1, s2 = p1, p2
    results = []

    for _ in range(3):
        lcs = longest_common_substring(s1, s2)
        results.append(lcs)

        if lcs:

            s1 = s1.replace(lcs, "", 1)
            s2 = s2.replace(lcs, "", 1)

    return results



def jaccard_similarity(p1, p2):
    set1, set2 = set(p1), set(p2)
    inter = len(set1 & set2)
    union = len(set1 | set2)
    return inter / union if union > 0 else 0.0



def process_file(input_csv="160kPosAndNegNoFeatures.csv", output_csv="160KLCSMS_FeaturedSet.csv"):
    rows_out = []

    with open(input_csv, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for row in reader:
            pw1 = row["Password1"]
            pw2 = row["Password2"]

            len1 = len(pw1)
            len2 = len(pw2)
            lendiff = abs(len1 - len2)
            avg_len = (len1 + len2) / 2


            lcs1, lcs2, lcs3 = lcs_three_parts(pw1, pw2)
            l1, l2, l3 = len(lcs1), len(lcs2), len(lcs3)


            r1 = l1 / avg_len if avg_len > 0 and l1 > 0 else 0
            r2 = l2 / avg_len if avg_len > 0 and l2 > 0 else 0
            r3 = l3 / avg_len if avg_len > 0 and l3 > 0 else 0


            lev = levenshtein_distance(pw1, pw2)
            shared_chars = len(set(pw1) & set(pw2))
            jaccard = jaccard_similarity(pw1, pw2)
            print(pw1 + "\t" + pw2 + "\t" + lcs1 + "\t" + lcs2 + "\t" + lcs3)
            rows_out.append({
                "Password1": pw1,
                "Password2": pw2,
                "Len1": len1,
                "Len2": len2,
                "LenDiff": lendiff,
                "LCS_1": l1,
                "LCS_2": l2,
                "LCS_3": l3,
                "LCS_Ratio_1": round(r1, 6),
                "LCS_Ratio_2": round(r2, 6),
                "LCS_Ratio_3": round(r3, 6),
                "Levenshtein": lev,
                "Shared_Chars": shared_chars,
                "Jaccard_Sim": round(jaccard, 6),
                "Label": row.get("Label", "")
            })


    fieldnames = [
        "Password1","Password2","Len1","Len2","LenDiff",
        "LCS_1","LCS_2","LCS_3",
        "LCS_Ratio_1","LCS_Ratio_2","LCS_Ratio_3",
        "Levenshtein","Shared_Chars","Jaccard_Sim","Label"
    ]

    with open(output_csv, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows_out)

    print(f" Saved processed file as: {output_csv}")
    print(f" Rows processed: {len(rows_out)}")


if __name__ == "__main__":
    process_file()
