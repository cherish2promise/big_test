# bogu_ai_module.py
import json
import logging
import time
from kafka import KafkaConsumer, KafkaProducer

# 모듈별 로깅 설정
logger = logging.getLogger("BoguAIModule")

KAFKA_BROKER = 'localhost:9092'
KAFKA_TOPIC = 'aivlebigproject'

def run_bogu_consumer_loop():
    consumer = None
    producer = None
    try:
        consumer = KafkaConsumer(
            KAFKA_TOPIC,
            bootstrap_servers=[KAFKA_BROKER],
            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
            auto_offset_reset='earliest',
            group_id='bogu-ai-group', # 각 서비스별 고유 그룹 ID 유지
            api_version=(0, 11, 0)
        )
        logger.info(f"Bogu AI 소비자 루프 시작 (그룹: bogu-ai-group)")

        producer = KafkaProducer(
            bootstrap_servers=[KAFKA_BROKER],
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            api_version=(0, 11, 0)
        )
        logger.info("Bogu AI 전용 Kafka Producer 초기화 완료.")

        for message in consumer:
            try:
                headers_dict = {header[0].decode('utf-8'): header[1].decode('utf-8') for header in message.headers}
                message_type = headers_dict.get('type')

                if message_type == "BoguAiRequest": # Bogu AI 요청 필터링
                    request_data = message.value
                    logger.info(f"Bogu AI 요청 수신: {request_data}")

                    original_request_id = request_data.get('id')
                    template_id = request_data.get('templateId')
                    time.sleep(1) # 처리 시뮬레이션
                    generated_file_url = f"http://s3.ai-storage.com/bogu-doc-{original_request_id}-{template_id}-{int(time.time())}.pdf"

                    response_payload = {
                        "id": original_request_id, "template_id": template_id,
                        "file_url": generated_file_url, "status": "completed", "processed_by": "BoguAI"
                    }
                    response_headers = [('type', b'BoguDocumentCreated')] # Bogu AI 응답 타입

                    producer.send(KAFKA_TOPIC, value=response_payload, headers=response_headers)
                    producer.flush()
                    logger.info(f"--> Bogu AI 응답 전송: ID {original_request_id}")
                else:
                    logger.debug(f"Bogu AI: 다른 메시지 타입 {message_type} 건너뜝니다.")
            except json.JSONDecodeError:
                logger.error(f"Bogu AI: JSON 디코딩 실패 - {message.value.decode('utf-8', errors='ignore')}", exc_info=True)
            except Exception as e:
                logger.error(f"Bogu AI: 메시지 처리 중 오류 발생 - {e}", exc_info=True)
    except Exception as e:
        logger.error(f"Bogu AI 소비자 루프 오류: {e}", exc_info=True)
    finally:
        if consumer: consumer.close(); logger.info("Bogu AI 소비자 종료")
        if producer: producer.close(); logger.info("Bogu AI Producer 종료")