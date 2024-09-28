import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from transformers import pipeline, logging
from PIL import Image
import sys
from fastapi import FastAPI
import requests
from io import BytesIO
logging.set_verbosity_error()

app = FastAPI()

predict = pipeline("image-classification", model="AdamCodd/vit-base-nsfw-detector")

def fetch_image(image_url: str):
    response = requests.get(image_url, stream=True, timeout=30)
    if response.status_code == 200:
        return Image.open(BytesIO(response.content))
    else:
        raise Exception(f"Failed to download image from {image_url}")

@app.get("/api/posts/check-nsfw")
async def check_nsfw(image_url: str):
    print(image_url)
    try:
        img = fetch_image(image_url)
        prediction = predict(img)

        is_nsfw = 1 if prediction[0]['label'] == 'nsfw' else 0

        return {'nsfw': is_nsfw}
    except Exception as e:
        return {"error": str(e)}