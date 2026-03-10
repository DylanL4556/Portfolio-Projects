import pandas as pd
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, accuracy_score
from xgboost.sklearn import XGBClassifier

df = pd.read_csv("160KLCSMS&EPSB_FeaturedSet.csv", header=0, low_memory=False)


numeric_cols = [
    "Len1","Len2","LenDiff","LCS_1","LCS_2","LCS_3","LCS_Ratio_1","LCS_Ratio_2","LCS_Ratio_3","Levenshtein","Shared_Chars","Jaccard_Sim",
    "caps1","low1","letters1","numbers1","symbols1","caps2","low2","letters2","numbers2","symbols2"
]


df[numeric_cols] = df[numeric_cols].apply(pd.to_numeric, errors="coerce")


df = df.dropna(subset=numeric_cols + ["Label"])


df["Label"] = df["Label"].astype(int)


X = df[numeric_cols]
y = df["Label"]


scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)


X_train, X_test, y_train, y_test = train_test_split(
    X_scaled, y, test_size=0.25, random_state=42, stratify=y
)


models = {
    "XGBClassifier": XGBClassifier(learning_rate=0.05,n_estimators=300,max_depth=4,subsample=0.8,colsample_bytree=0.8,objective='binary:logistic',n_jobs=-1),
    #"XGBClassifier2": XGBClassifier(learning_rate=0.1, n_estimators=100, max_depth=5,objective='binary:logistic')
    #"Logistic Regression": LogisticRegression(max_iter=1000),
    #"Random Forest": RandomForestClassifier(n_estimators=300, random_state=42),
    #"Gradient Boosting": GradientBoostingClassifier(random_state=42)
}

for name, model in models.items():
    model.fit(X_train, y_train)
    preds = model.predict(X_test)
    acc = accuracy_score(y_test, preds)
    print(f"\n{name} Accuracy: {acc:.3f}")
    print(classification_report(y_test, preds))

