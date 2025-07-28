# timetable_ai_module.py
import json
import logging
import time
from kafka import KafkaConsumer, KafkaProducer

# 모듈별 로깅 설정
logger = logging.getLogger("TimetableAIModule")

KAFKA_BROKER = 'localhost:9092'
KAFKA_TOPIC = 'aivlebigproject'

def run_timetable_consumer_loop():
    consumer = None
    producer = None
    try:
        consumer = KafkaConsumer(
            KAFKA_TOPIC,
            bootstrap_servers=[KAFKA_BROKER],
            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
            auto_offset_reset='earliest',
            group_id='timetable-ai-group', # 각 서비스별 고유 그룹 ID 유지
            api_version=(0, 11, 0)
        )
        logger.info(f"Timetable AI 소비자 루프 시작 (그룹: timetable-ai-group)")

        producer = KafkaProducer(
            bootstrap_servers=[KAFKA_BROKER],
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            api_version=(0, 11, 0)
        )
        logger.info("Timetable AI 전용 Kafka Producer 초기화 완료.")

        for message in consumer:
            try:
                headers_dict = {header[0].decode('utf-8'): header[1].decode('utf-8') for header in message.headers}
                message_type = headers_dict.get('type')

                if message_type == "TimetableAiRequest":
                    request_data = message.value
                    logger.info(f"Timetable AI 요청 수신: {request_data}")

                    original_request_id = request_data.get('id')
                    template_id = request_data.get('templateId')
                    time.sleep(2) # 처리 시뮬레이션
                    generated_file_url = f"http://s3.ai-storage.com/timetable-doc-{original_request_id}-{template_id}-{int(time.time())}.pdf"

                    response_payload = {
                        "id": original_request_id, "template_id": template_id,
                        "file_url": generated_file_url, "status": "completed", "processed_by": "TimetableAI"
                    }
                    response_headers = [('type', b'TimetableDocumentCreated')]

                    producer.send(KAFKA_TOPIC, value=response_payload, headers=response_headers)
                    producer.flush()
                    logger.info(f"--> Timetable AI 응답 전송: ID {original_request_id}")
                else:
                    logger.debug(f"Timetable AI: 다른 메시지 타입 {message_type} 건너뜀")
            except json.JSONDecodeError:
                logger.error(f"Timetable AI: JSON 디코딩 실패 - {message.value.decode('utf-8', errors='ignore')}", exc_info=True)
            except Exception as e:
                logger.error(f"Timetable AI: 메시지 처리 중 오류 발생 - {e}", exc_info=True)
    except Exception as e:
        logger.error(f"Timetable AI 소비자 루프 오류: {e}", exc_info=True)
    finally:
        if consumer: consumer.close(); logger.info("Timetable AI 소비자 종료")
        if producer: producer.close(); logger.info("Timetable AI Producer 종료")