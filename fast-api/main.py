import asyncio
from fastapi import FastAPI
from bogo_ai_consumer import consume # <-- 파일 이름 변경 반영
from samang_ai_consumer import consume_samang_ai
from timetable_ai_consumer import consume_timetable_ai

app = FastAPI()

@app.get("/")
def root():
    return {"message": "All Kafka AI consumers running (Bogu, Samang, Timetable)"}

@app.on_event("startup")
async def startup_event():
    loop = asyncio.get_event_loop()
    # Bogu AI 컨슈머 시작
    loop.create_task(consume())
    print("Bogu AI consumer task created.")

    # Samang AI 컨슈머 시작
    loop.create_task(consume_samang_ai())
    print("Samang AI consumer task created.")

    # Timetable AI 컨슈머 시작
    loop.create_task(consume_timetable_ai())
    print("Timetable AI consumer task created.")

    print("FastAPI application startup tasks initialized.")