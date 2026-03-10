import re
import trafilatura
import requests
import xml.etree.ElementTree as ET
from urllib.parse import urlparse, urlunparse
from bs4 import BeautifulSoup

# sitemap.xml is fetching links without www, these are timing out, but their www. version works.
def normalize_url(url):
    parsed = urlparse(url)

    # If hostname exists and doesn't start with www
    if parsed.netloc and not parsed.netloc.startswith("www."):
        new_netloc = "www." + parsed.netloc
        parsed = parsed._replace(netloc=new_netloc)

    return urlunparse(parsed)

# Saving logic for urls
def url_to_filename(url):
    parsed = urlparse(url)

    path = parsed.path.strip("/")
    if not path:
        path = "homepage"

    filename = re.sub(r"[^a-zA-Z0-9_-]", "_", path)

    return f"{filename}.txt"

def extract_text(html):
    extrctd = trafilatura.extract(html)
    return extrctd if extrctd else ""

def extract_title(html):
    soup = BeautifulSoup(html, "lxml")
    if soup.title:
        return soup.title.get_text(strip=True)
    return ""

sitemap_url = "https://www.usca.edu/sitemap.xml"

response = requests.get(sitemap_url)
root = ET.fromstring(response.content)

ns = {"sm": "http://www.sitemaps.org/schemas/sitemap/0.9"}

urls = []

for url in root.findall("sm:url", ns):
    loc = url.find("sm:loc", ns)
    if loc is not None:
        urls.append(loc.text)

print(f"Found {len(urls)} URLs")
print(urls[:5])

for link in urls:
    link = normalize_url(link)
    try:
        page = requests.get(link, timeout=10)
        print(f"Fetched: {link} ({page.status_code})")
        extracted = extract_text(page.content)
        text = extract_text(page.text)
        print(f"Extracted: {link}")
        filename = url_to_filename(link)
        title = extract_title(page.text)
        urll = link
        with open(("website-content2/" + filename), "w") as f:
            f.write(title + "\n")
            f.write(urll + "\n")
            f.write(text)
        print(f"Written: {filename}")
    except Exception as e:
        print(f"Error fetching {link}: {e}")

