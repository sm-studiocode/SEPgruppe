/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 4. 8.     	SHJ            최초 생성
 *
 * </pre>
 */

if (typeof alarmConnected === 'undefined') {
  var alarmConnected = false;
}
// 🔔 Notification 유틸리티 함수
function showPushNotification(title, content, onClickCallback) {
	
	// 🔔 알림 권한 요청
    if (Notification.permission !== "granted") {
        Notification.requestPermission().then(function (permission) {
            console.log("알림 권한 상태:", permission);
        });
    }
		
    if (Notification.permission === "granted") {
        const notification = new Notification(title, {
            body: content
        });

        // 클릭 이벤트 처리
        notification.onclick = function(event) {
            event.preventDefault();
            if (typeof onClickCallback === "function") {
                onClickCallback();
            }
			notification.close();
        };
    }
}
function connectAlarm(userId) {
	
	if (alarmConnected) return;
	
    const socket = new SockJS("/sep/stomp");
    alarmClient = Stomp.over(socket);

    alarmClient.connect({}, function(frame) {
        console.log("🔔 알림 연결 완료");

        // 예: /topic/alarm/{userId} 구독
    alarmClient.subscribe("/topic/alarm/" + userId, function(message) {
        const alarm = JSON.parse(message.body);
		
		// 자기 자신이 보낸 메시지는 제외하고 알림 표시
		if (alarm.senderEmpId !== userId) {
			const senderName = alarm.organization?.empNm || "알 수 없음";
			const chatroomId = alarm.roomId; // 메시지에 포함된 채팅방 ID

			showPushNotification("📬 새 메시지", senderName + ": " + alarm.msgContent, function() {
				openChatRoom(chatroomId); // 👈 클릭 시 이 함수 실행
			});
		}
    });
	
	alarmClient.subscribe("/topic/project/" + userId, function(message) {
	    const projectAlarm = JSON.parse(message.body);
	    showPushNotification("📋 공유프로젝트에 참가되었습니다.", projectAlarm.alarmNm, function() {
	        openProjectPage(projectAlarm.alarmCategoryNo);
	    });
	});
	
	alarmClient.subscribe("/topic/task/" + userId, function(message) {
	    const taskAlarm = JSON.parse(message.body);
	    showPushNotification("📋 프로젝트에 일감이 부여되었습니다.", " " ,function() {
	        openTaskPage(taskAlarm.alarmCategoryNo);
	    });
	});
	
	alarmClient.subscribe("/topic/appr/" + userId, function(message) {
	    const apprAlarm = JSON.parse(message.body);
	    showPushNotification("📋 전자결재 결재요청.",apprAlarm.aprvlDocTitle , function() {
	        openApprPage(apprAlarm.alarmContent);
	    });
	});
	
	alarmClient.subscribe("/topic/community/" + userId, function(message) {
	    const communityAlarm = JSON.parse(message.body);
	    showPushNotification("📋 커뮤니티 가입요청.", "" , function() {
	        opencommunityPage(communityAlarm.alarmCategoryNo);
	    });
	});
	
	alarmClient.subscribe("/topic/community2/" + userId, function(message) {
	    const communityAlarm = JSON.parse(message.body);
	    showPushNotification("📋 커뮤니티 가입 승인되었습니다.", "" , function() {
	        opencommunityPage(communityAlarm.alarmCategoryNo);
	    });
	});
	
	alarmClient.subscribe("/topic/notice/" + userId, function(message) {
	    const noticeAlarm = JSON.parse(message.body);
	    showPushNotification("📋 전사 공지사항이 게시되었습니다.", "" , function() {
	        openNoticePage(noticeAlarm.alarmCategoryNo);
	    });
	});
	
	
    });
}

function openChatRoom(chatroomId) {
    openMessengerWindow(); // 1. 메신저 창 열기

	setTimeout(function () {
	        if (sepMessenger.$("#listLink").length) {
	            sepMessenger.$("#listLink")[0].click();
	        }

	        // 3. 채팅방 리스트 로딩 대기
	        waitForRoomToAppear(chatroomId, 0); // roomId를 가진 방이 나올 때까지 대기
	    }, 300);
}

function openProjectPage(projectId) {
    window.open("/sep/testnum001/project/" + projectId, "_blank");
}

function openTaskPage(taskId) {
    window.open("/sep/testnum001/task/" + taskId, "_blank");
}

function openApprPage(apprId) {
    window.open("/sep/testnum001/apprHome/aprvlDocDetail/" + apprId, "_blank");
}

function opencommunityPage(communityId) {
    window.open("/sep/testnum001/community", "_blank");
}

function openNoticePage(noticeId) {
    window.open("/sep/testnum001/notice/" + noticeId, "_blank");
}


function waitForRoomToAppear(roomId, tryCount) {
    const targetRoom = sepMessenger.$("li.chat-room-row[data-roomid='" + roomId + "']");

    if (targetRoom.length) {
        console.log("✅ 방 찾음:", roomId);
        targetRoom.trigger("click");
    } else if (tryCount < 20) {
        // 아직 방이 안 뜬 경우, 0.3초 후 재시도 (최대 20번 = 약 6초 대기)
        setTimeout(function () {
            waitForRoomToAppear(roomId, tryCount + 1);
        }, 300);
    } else {
        console.warn("❗ 채팅방을 찾지 못했습니다:", roomId);
    }
}

$(document).ready(function () {
    const alarmUserId = $(".main-panel").data("emp-id"); // 로그인한 사용자 ID 가져오기
    connectAlarm(alarmUserId); // 알림 구독 시작
});