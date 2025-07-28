from fastapi import FastAPI, BackgroundTasks, HTTPException
import threading
import uvicorn
import logging
import time

from kafka_consumer_producer import close_producer # Kafka Producer 종료 함수 임포트
from bogo_ai_consumer import start_bogo_consumer # 각 AI 서비스의 소비자 시작 함수 임포트
from samang_ai_consumer import start_samang_consumer
from timetable_ai_consumer import start_timetable_consumer

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = FastAPI()

# Kafka Consumer 스레드들을 저장할 리스트
consumer_threads = []

@app.on_event("startup")
async def startup_event():
    logger.info("FastAPI application starting up...")

    # 부고장 AI 소비자 시작
    t1 = threading.Thread(target=start_bogo_consumer, daemon=True) # daemon=True로 설정하여 메인 스레드 종료 시 함께 종료
    t1.start()
    consumer_threads.append(t1)
    logger.info("Bogo AI Consumer thread started.")

    # 사망신고서 AI 소비자 시작
    t2 = threading.Thread(target=start_samang_consumer, daemon=True)
    t2.start()
    consumer_threads.append(t2)
    logger.info("Samang AI Consumer thread started.")
    
    # 장례일정표 AI 소비자 시작
    t3 = threading.Thread(target=start_timetable_consumer, daemon=True)
    t3.start()
    consumer_threads.append(t3)
    logger.info("Timetable AI Consumer thread started.")

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("FastAPI application shutting down...")
    
    # Kafka Producer 종료
    close_producer()

    # Consumer 스레드는 daemon=True로 설정했기 때문에 명시적으로 종료할 필요는 없지만,
    # 필요하다면 join() 등을 사용하여 기다릴 수 있습니다.
    logger.info("All consumer threads should terminate gracefully.")

@app.get("/")
async def root():
    return {"message": "AI services are running and listening to Kafka."}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)