import asyncio
from aiokafka import AIOKafkaConsumer
import json

KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
TIMETABLE_AI_TOPIC = "timetable-ai-request-topic" # application.yml에 설정된 토픽 이름과 일치

async def handle_timetable_ai_request(data: dict):
    """
    Timetable AI 서비스의 실제 처리 로직을 담당합니다.
    FuneralRegiste 이벤트 데이터를 받아서 필요한 AI 작업을 수행합니다.
    """
    print("\n--- Timetable AI: Received message from Kafka ---")
    print(f"  Topic: {TIMETABLE_AI_TOPIC}")
    print(f"  Payload: {json.dumps(data, indent=2, ensure_ascii=False)}")

    # 여기에 실제 Timetable AI 로직 삽입
    # 예: 장례식 날짜/시간, 사용자 요구사항 등을 바탕으로 일정 생성
    funeral_id = data.get("id")
    procession_datetime = data.get("processionDateTime") # 발인 일시 등
    # ... FuneralRegiste 이벤트의 다른 필드들을 data 딕셔너리에서 추출하여 사용

    print(f"  Timetable AI processing data for Funeral ID: {funeral_id}, Procession Time: {procession_datetime}...")
    # 가정: Timetable AI가 장례 일정표를 생성했다고 가정
    print("  Timetable AI: 장례 일정표 생성 완료 (가정)")
    # AI 처리 결과를 다시 Kafka로 발행하여 Spring Boot에 알릴 수도 있습니다.
    # (이 부분은 추후 필요시 구현)

    print("---------------------------------------------------\n")

async def consume_timetable_ai():
    """
    Timetable AI 서비스의 Kafka 컨슈머를 시작하고 메시지를 처리합니다.
    """
    consumer = AIOKafkaConsumer(
        TIMETABLE_AI_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="timetable-ai-consumer-group", # 이 컨슈머 그룹의 고유 ID
        value_deserializer=lambda x: json.loads(x.decode("utf-8"))
    )

    print(f"Starting Timetable AI Kafka consumer on topic: {TIMETABLE_AI_TOPIC}")
    await consumer.start()
    try:
        async for msg in consumer:
            await handle_timetable_ai_request(msg.value)
    except asyncio.CancelledError:
        print(f"Timetable AI Kafka consumer for {TIMETABLE_AI_TOPIC} cancelled.")
    except Exception as e:
        print(f"Timetable AI Kafka consumer error: {e}")
    finally:
        await consumer.stop()
        print(f"Timetable AI Kafka consumer for {TIMETABLE_AI_TOPIC} stopped.")

# 이 파일이 직접 실행될 때 (테스트용)
if __name__ == "__main__":
    asyncio.run(consume_timetable_ai())