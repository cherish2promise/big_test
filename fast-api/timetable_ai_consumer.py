import logging
import json
import uuid
import time

from kafka_consumer_producer import start_consumer, send_message

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

KAFKA_INPUT_TOPIC = "timetable-ai-request" # Spring Boot의 outboundTimetableAiRequest 에 해당
KAFKA_OUTPUT_TOPIC = "timetable-ai-processed" # KafkaProcessor.INPUT_TIMETABLE_AI_PROCESSED 에 해당

CONSUMER_GROUP_ID = "timetable-ai-group"
TEMPLATE_ID = 3 # 장례 일정표

def process_timetable_request(message_data: dict):
    """장례 일정표 생성 요청 메시지를 처리하는 함수."""
    original_request_id = message_data.get("id")
    if not original_request_id:
        logger.error(f"Received message without 'id': {message_data}")
        return

    logger.info(f"Processing timetable request for original ID: {original_request_id}")

    generated_file_url = None
    try:
        # ----------------------------------------------------------------------
        # ✨ 여기부터 실제 AI 모델 처리 및 파일 저장 로직 ✨
        # ----------------------------------------------------------------------
        time.sleep(5) # 시뮬레이션
        filename = f"timetable_{original_request_id}_{uuid.uuid4()}.pdf"
        generated_file_url = f"http://your-file-server.com/documents/{filename}" 
        logger.info(f"Timetable for request ID {original_request_id} generated. File URL: {generated_file_url}")
        # ----------------------------------------------------------------------
        # ✨ 실제 AI 로직 끝 ✨
        # ----------------------------------------------------------------------

        output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": generated_file_url,
            "message": "장례 일정표 생성이 성공적으로 완료되었습니다."
        }
        send_message(KAFKA_OUTPUT_TOPIC, str(original_request_id), output_payload)
        logger.info(f"Sent completion message for timetable (ID: {original_request_id}) to Spring Boot.")

    except Exception as e:
        logger.error(f"Error during timetable generation for ID {original_request_id}: {e}", exc_info=True)
        error_output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": None,
            "error": str(e),
            "message": "장례 일정표 생성에 실패했습니다."
        }
        send_message(KAFKA_OUTPUT_TOPIC, str(original_request_id), error_output_payload)
        logger.error(f"Sent error message for timetable (ID: {original_request_id}) to Spring Boot.")

def start_timetable_consumer():
    """장례 일정표 AI Consumer를 시작하는 헬퍼 함수."""
    start_consumer(KAFKA_INPUT_TOPIC, CONSUMER_GROUP_ID, process_timetable_request)

if __name__ == "__main__":
    logger.info("Starting Timetable AI Consumer directly...")
    start_timetable_consumer()