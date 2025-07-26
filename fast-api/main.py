import asyncio
from fastapi import FastAPI
from consumer import consume

app = FastAPI()

@app.get("/")
def root():
    return {"message": "Kafka funeral-request consumer running"}

@app.on_event("startup")
async def startup_event():
    loop = asyncio.get_event_loop()
    loop.create_task(consume())