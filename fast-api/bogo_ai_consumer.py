import asyncio
from aiokafka import AIOKafkaConsumer
import json

KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
TOPIC_NAME = "funeral-request"

async def handle_funeral_request(data: dict):
    print(" Received funeral request data:")
    print(json.dumps(data, indent=2, ensure_ascii=False))

    print(" 문서 생성 완료 (가정)")

async def consume():
    consumer = AIOKafkaConsumer(
        TOPIC_NAME,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="fastapi-funeral-consumer",
        value_deserializer=lambda x: json.loads(x.decode("utf-8"))
    )

    # Start the consumer
    await consumer.start()
    try:
        async for msg in consumer:
            await handle_funeral_request(msg.value)
    finally:
        await consumer.stop()