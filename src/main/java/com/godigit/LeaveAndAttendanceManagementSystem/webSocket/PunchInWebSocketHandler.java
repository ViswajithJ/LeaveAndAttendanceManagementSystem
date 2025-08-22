package com.godigit.LeaveAndAttendanceManagementSystem.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class PunchInWebSocketHandler extends TextWebSocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final AttendanceService attendanceService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("WebSocket message received: " + payload);

        JsonNode jsonNode = mapper.readTree(payload);

        Long userId = jsonNode.get("userId").asLong();
        String punchType = jsonNode.get("punchType").asText();

        Attendance attendance;

        if ("IN".equalsIgnoreCase(punchType)) {
            attendance = attendanceService.punchIn(userId);
        } else if ("OUT".equalsIgnoreCase(punchType)) {
            attendance = attendanceService.punchOut(userId);
        } else {
            session.sendMessage(new TextMessage("Invalid punch type. Use 'IN' or 'OUT'."));
            return;
        }

        session.sendMessage(new TextMessage("Attendance " + punchType + " recorded successfully."));
    }
}
