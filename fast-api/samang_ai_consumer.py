import asyncio
from aiokafka import AIOKafkaConsumer
import json

KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
SAMANG_AI_TOPIC = "samang-ai-request-topic" # application.yml에 설정된 토픽 이름과 일치

async def handle_samang_ai_request(data: dict):
    """
    Samang AI 서비스의 실제 처리 로직을 담당합니다.
    FuneralRegiste 이벤트 데이터를 받아서 필요한 AI 작업을 수행합니다.
    """
    print("\n--- Samang AI: Received message from Kafka ---")
    print(f"  Topic: {SAMANG_AI_TOPIC}")
    print(f"  Payload: {json.dumps(data, indent=2, ensure_ascii=False)}")

    # 여기에 실제 Samang AI 로직 삽입
    # 예: 데이터 분석, 특정 정보 추출, DB 저장 등
    funeral_id = data.get("id")
    funeral_name = data.get("name")
    funeral_rrn = data.get("rrn") # 주민등록번호 등
    # ... FuneralRegiste 이벤트의 다른 필드들을 data 딕셔너리에서 추출하여 사용

    print(f"  Samang AI processing data for Funeral ID: {funeral_id}, Name: {funeral_name}...")
    # 가정: Samang AI가 특정 문서를 생성하거나 정보를 분석했다고 가정
    print("  Samang AI: 사망자 정보 분석 및 관련 문서 준비 완료 (가정)")
    # AI 처리 결과를 다시 Kafka로 발행하여 Spring Boot에 알릴 수도 있습니다.
    # (이 부분은 추후 필요시 구현)

    print("---------------------------------------------------\n")

async def consume_samang_ai():
    """
    Samang AI 서비스의 Kafka 컨슈머를 시작하고 메시지를 처리합니다.
    """
    consumer = AIOKafkaConsumer(
        SAMANG_AI_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="samang-ai-consumer-group", # 이 컨슈머 그룹의 고유 ID
        value_deserializer=lambda x: json.loads(x.decode("utf-8"))
    )

    print(f"Starting Samang AI Kafka consumer on topic: {SAMANG_AI_TOPIC}")
    await consumer.start()
    try:
        async for msg in consumer:
            await handle_samang_ai_request(msg.value)
    except asyncio.CancelledError:
        print(f"Samang AI Kafka consumer for {SAMANG_AI_TOPIC} cancelled.")
    except Exception as e:
        print(f"Samang AI Kafka consumer error: {e}")
    finally:
        await consumer.stop()
        print(f"Samang AI Kafka consumer for {SAMANG_AI_TOPIC} stopped.")

# 이 파일이 직접 실행될 때 (테스트용)
if __name__ == "__main__":
    asyncio.run(consume_samang_ai())