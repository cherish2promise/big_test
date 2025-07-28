# main.py
from fastapi import FastAPI
import logging
import threading
import uvicorn
import sys # 로깅 설정을 위한 sys.stdout 사용

# 분리된 AI 모듈들을 임포트합니다.
from time_ai import run_timetable_consumer_loop
from bugo_ai import run_bogu_consumer_loop
from samang_ai import run_samang_consumer_loop


# --- 1. 로깅 설정 ---
# 모든 로깅 메시지를 콘솔에 출력하도록 설정합니다.
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    stream=sys.stdout # 로그를 표준 출력으로 보냅니다.
)
logger = logging.getLogger("MainFastAPIApp") # 통합 앱의 메인 로거


# --- 2. FastAPI 애플리케이션 초기화 ---
app = FastAPI(title="통합 AI 서비스 애플리케이션")

# 각 AI 서비스의 소비자 스레드를 저장할 리스트
consumer_threads = []

# --- 3. FastAPI 시작/종료 이벤트 핸들러 ---
@app.on_event("startup")
async def startup_event():
    logger.info("FastAPI 통합 애플리케이션 시작 이벤트가 트리거되었습니다.")

    # 각 AI 서비스의 소비자 루프를 별도의 스레드에서 시작합니다.
    # 데몬 스레드는 메인 애플리케이션(FastAPI)이 종료될 때 자동으로 종료됩니다.

    # Timetable AI 소비자 스레드 시작
    timetable_thread = threading.Thread(target=run_timetable_consumer_loop, daemon=True, name="TimetableAIConsumer")
    consumer_threads.append(timetable_thread)
    timetable_thread.start()
    logger.info(f"{timetable_thread.name} 스레드가 백그라운드에서 시작되었습니다.")

    # Bogu AI 소비자 스레드 시작
    bogu_thread = threading.Thread(target=run_bogu_consumer_loop, daemon=True, name="BoguAIConsumer")
    consumer_threads.append(bogu_thread)
    bogu_thread.start()
    logger.info(f"{bogu_thread.name} 스레드가 백그라운드에서 시작되었습니다.")

    # Samang AI 소비자 스레드 시작
    samang_thread = threading.Thread(target=run_samang_consumer_loop, daemon=True, name="SamangAIConsumer")
    consumer_threads.append(samang_thread)
    samang_thread.start()
    logger.info(f"{samang_thread.name} 스레드가 백그라운드에서 시작되었습니다.")

    logger.info("모든 AI 서비스 소비자 스레드가 시작되었습니다.")


@app.on_event("shutdown")
async def shutdown_event():
    logger.info("FastAPI 통합 애플리케이션 종료 이벤트가 트리거되었습니다.")
    # (선택 사항: 각 소비자 스레드가 종료될 때까지 기다릴 수도 있습니다.
    #  데몬 스레드이므로 앱 종료 시 자동으로 종료되지만, 명시적 정리가 필요할 때 아래 코드를 사용)
    # for thread in consumer_threads:
    #     if thread.is_alive():
    #         logger.info(f"스레드 {thread.name} 종료 대기...")
    #         thread.join(timeout=5) # 5초 대기
    #         if thread.is_alive():
    #             logger.warning(f"스레드 {thread.name}가 정상 종료되지 않았습니다.")

    logger.info("FastAPI 통합 애플리케이션 종료가 완료되었습니다.")


# --- 4. 통합 AI 서비스 엔드포인트 ---
# 메인 애플리케이션에 대한 기본 응답 엔드포인트
@app.get("/")
async def root():
    return {"message": "통합 AI 서비스 애플리케이션이 실행 중입니다."}

# 모든 AI 서비스의 헬스 체크 상태를 확인할 수 있는 엔드포인트
@app.get("/health")
async def health_check():
    # 각 소비자 스레드의 현재 활성 상태를 확인하여 반환합니다.
    thread_statuses = {
        thread.name: thread.is_alive() for thread in consumer_threads
    }
    return {"status": "ok", "service": "통합 AI 서비스", "consumer_threads": thread_statuses}


# --- 5. main 함수와 실행 진입점 ---
def main():
    logger.info("main.py를 통해 통합 FastAPI 애플리케이션을 시작합니다.")
    # uvicorn 서버를 시작하여 FastAPI 앱을 서비스합니다.
    uvicorn.run(app, host="0.0.0.0", port=8000)

if __name__ == "__main__":
    main()
