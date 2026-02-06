/** 
 * <pre>
 * << 개정이력(Modification Information) >
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 3. 26.     	JSW            최초 생성
 *
 * </pre>
 */
document.addEventListener("DOMContentLoaded", () => {

	// FancyTree 초기화
	$("#depTree").fancytree({
		source: {
			url: contextPath + "/organization/admin/parentDep",
			cache: false
		},
		lazyLoad: function (event, data) {
			var node = data.node;
			let mode = "employee";
			if (!node.data.parentDeptCd) {
				mode = "department";
			}
			// Load child nodes via Ajax
			data.result = {
				url: contextPath + "/organization/admin/childeDep",
				data: { mode: mode, parent: node.key },
				cache: false
			};
		},
		renderNode: function (event, data) {
			var node = data.node;
			var $span = $(node.span);

			// 기존 아이콘 제거 (중복 방지)
			$span.find(".fancytree-icon").remove();

			if (node.data.empNm) {
				// 사원 노드
				const isManager = node.parent && node.parent.data.managerEmpId === node.data.empId;
				const iconClass = isManager ? "fas fa-user-tie" : "fas fa-user";
				const iconHtml = `<i class="${iconClass} fancytree-icon"></i>`;

				$span.find(".fancytree-title").html(
					`${iconHtml} ${node.data.empNm} (${node.data.positionName})`
				);
			} else {
				// 부서 노드
				$span.find(".fancytree-title").prepend(
					`<i class="fas fa-building fancytree-icon"></i> `
				);
			}
		},
		activate: function (event, data) {
			var node = data.node;
			if (node.data.empNm) {
				// 사원 정보 표시
				showEmployeeDetail(node.data);
			} else {
				// 부서 정보 표시
				showDepartmentDetail(node.data);
			}
		}
	});

	function showEmployeeDetail(employee) {
		let detailHtml = `
			<div class="employee-detail">
				<h3>${employee.empNm} ${employee.positionName}</h3>
				<p><strong>사원번호 : </strong> ${employee.empNo}</p>
				<p><strong>이메일 : </strong> ${employee.empEmail || '-'}</p>
				<p><strong>전화번호 : </strong> ${employee.empPhone || '-'}</p>
				<p><strong>입사일 : </strong> ${employee.empRegdate || '-'}</p>
			</div>
		`;
		$("#detailContent").html(detailHtml);
	}

	function showDepartmentDetail(department) {

		let detailHtml = `
			<h4 class="mb-3">${department.deptName}</h4>
			<div class="department-detail">
				<p>
					<strong>부서명 : </strong>
					<span id="deptNameText">${department.deptName}</span>
					<img src="${contextPath}/resources/groupware/images/edit.png" class="edit-icon" data-field="deptName">
				</p>
				<p>
					<strong>부서코드 : </strong>
					<span id="deptCdText">${department.deptCd}</span>
				</p>
				<p>
					<strong>부서장 ID : </strong>
					<span id="managerEmpIdText">${department.managerEmpId || '-'}</span>
					<img src="${contextPath}/resources/groupware/images/edit.png" class="edit-icon" data-field="managerEmpId">
				</p>
			</div>
		`;

		// 하위 부서 불러오기
		$.ajax({
			type: "GET",
			url: `${contextPath}/organization/admin/childDepartments`,
			data: { parentDeptCd: department.deptCd },
			success: function (children) {
				if (children.length > 0) {
					detailHtml += `
						<div class="sub-dept-wrapper">
							<span class="sub-dept-label"><strong>하위 부서 : </strong></span>
							<ul class="sub-dept-list">
					`;
					children.forEach(dept => {
						detailHtml += `<li class="dept-badge">${dept.deptName}</li>`;
					});
					detailHtml += `</ul>`;
				} else {
					detailHtml += `<p><strong>하위 부서 :</strong> 미지정</p>`;
				}

				detailHtml += `</div>`;
				$("#detailContent").html(detailHtml);
			},
			error: function () {
				detailHtml += `<p><strong>하위 부서 : </strong> 불러오기 실패</p></div>`;
				$("#detailContent").html(detailHtml);
			}
		});
	}

	$(document).on("click", ".edit-icon", function () {
		const field = $(this).data("field");
		const span = $(`#${field}Text`);
		const currentValue = span.text().trim();

		if (field === "managerEmpId") {
			// 부서장 수정일 경우 select로 처리
			$.ajax({
				type: "GET",
				url: `${contextPath}/organization/admin/employees`,
				success: function (employees) {
					const select = $('<select id="managerEmpIdInput"></select>');
					select.append(`<option value="">선택하세요</option>`);

					employees.forEach(emp => {
						const optionText = `${emp.empNm} (${emp.empId})`;
						const selected = emp.empId === currentValue ? "selected" : "";
						select.append(`<option value="${emp.empId}" ${selected}>${optionText}</option>`);
					});

					span.replaceWith(select);
					select.focus();

					// 변경 시 즉시 저장
					select.on("change", function () {
						const newValue = $(this).val();
						updateDepartmentField("managerEmpId", newValue);
					});

					// 포커스 아웃 시 저장
					select.on("blur", function () {
						const newValue = $(this).val();
						updateDepartmentField("managerEmpId", newValue);
					});
				}
			});

		} else {
			// 일반 텍스트 필드는 input 처리
			const input = $(`<input type="text" id="${field}Input" value="${currentValue}"/>`);
			span.replaceWith(input);
			input.focus();

			input.on("keyup", function (e) {
				if (e.key === "Enter") {
					const newValue = input.val().trim();
					updateDepartmentField(field, newValue);
				}
			});

			input.on("blur", function () {
				const newValue = input.val().trim();
				updateDepartmentField(field, newValue);
			});
		}
	});

	function updateDepartmentField(field, newValue) {

		const deptCd = $("#deptCdText").text().trim();

		const payload = {
			deptCd: deptCd
		};
		payload[field] = newValue;

		$.ajax({
			type: "PATCH",
			url: `${contextPath}/department/updateField`,
			contentType: "application/json",
			data: JSON.stringify(payload),
			success: function () {
				Swal.fire({
					toast: true,
					position: 'top',
					icon: "success",
					title: "수정 완료",
					text: "변경사항이 저장되었습니다.",
					showConfirmButton: false,
					timer: 2000
				});

				// 조직도 트리 새로고침
				$("#depTree").fancytree("getTree").reload({
					url: `${contextPath}/organization/admin/parentDep`,
					cache: false
				});

				// 상세보기 초기화
				$("#detailContent").html(`<p>부서 또는 사원을 선택해주세요.</p>`);
			},
			error: function () {
				Swal.fire({
					toast: true,
					position: 'top',
					icon: "error",
					title: "수정 실패",
					text: "수정 중 오류가 발생했습니다.",
					showConfirmButton: false,
					timer: 2000
				});
			}
		});
	}

	$("#search-btn").on("click", function () {

		var keyword = $("#employee-search").val().trim();

		if (!keyword) {
			Swal.fire({
				toast: true,
				position: 'top',
				icon: 'warning',
				title: '검색어 누락',
				text: '검색어를 입력해주세요.',
				showConfirmButton: false,
				timer: 2000
			});
			return;
		}

		$.ajax({
			url: contextPath + "/organization/admin/search",
			type: "GET",
			data: { keyword },
			success: function (data) {
				renderTree(data);
			},
			error: function () {
				Swal.fire({
					toast: true,
					position: 'top',
					icon: 'error',
					title: '검색 실패',
					html: `오류가 발생했습니다.`,
					showConfirmButton: false,
					timer: 2000
				});
			}
		});
	});

	$("#employee-search").on("keyup", function (e) {
		const keyword = $(this).val().trim();

		if (e.key === "Enter") {
			$("#search-btn").click();
		}
		if (keyword === "") {
			$("#depTree").fancytree("getTree").reload({
				url: `${contextPath}/organization/admin/parentDep`,
				cache: false
			});
		}
	});

	// 부서 추가 모달 열기
	$('#add-dept-btn').on('click', function () {
		$('#addDeptModal').fadeIn();
		loadEmployeeListForManager(); // 부서장 후보
	});

	// 모달 닫기
	$('#cancelAddDept, .close-modal').on('click', function () {
		$('#addDeptModal').fadeOut();
	});

	// 모달 외부 클릭 시 닫기
	$(window).on('click', function (e) {
		if ($(e.target).is('#addDeptModal')) {
			$('#addDeptModal').fadeOut();
		}
	});

	function loadEmployeeListForManager() {
		$.ajax({
			type: "GET",
			url: `${contextPath}/organization/admin/employees`,
			success: function (employees) {
				const $select = $("#managerEmpId");
				$select.empty().append(`<option value="">선택하세요</option>`);
				employees.forEach(emp => {
					const optionText = `${emp.empNm} (${emp.empId})`;
					$select.append(`<option value="${emp.empId}">${optionText}</option>`);
				});
			},
			error: function () {
				Swal.fire({
					toast: true,
					position: 'top',
					icon: 'error',
					title: '사원 목록 로딩 실패',
					text: '부서장 선택을 위해 사원 목록을 불러오는 데 실패했습니다.',
					showConfirmButton: false,
					timer: 2000
				});
			}
		});
	}

	// 부서 추가 폼 submit
	$('#addDeptForm').on('submit', function (e) {
		e.preventDefault();

		const deptData = {
			deptCd: $('#deptCd').val(),
			deptName: $('#deptName').val(),
			parentDeptCd: $('#parentDeptCd').val(),
			managerEmpId: $('#managerEmpId').val()
		};

		$.ajax({
			type: 'POST',
			url: `${contextPath}/department/new/dept`,
			contentType: 'application/json',
			data: JSON.stringify(deptData),
			success: function () {
				$('#addDeptModal').fadeOut(200, function () {
					Swal.fire({
						toast: true,
						position: 'top',
						icon: 'success',
						title: '등록 완료',
						text: '부서가 성공적으로 추가되었습니다.',
						showConfirmButton: false,
						timer: 2000
					});

					$("#depTree").fancytree("getTree").reload({
						url: `${contextPath}/organization/admin/parentDep`,
						cache: false
					});
				});
			},
			error: function () {
				Swal.fire({
					toast: true,
					position: 'top',
					icon: 'error',
					title: '등록 실패',
					text: '부서 등록 중 오류가 발생했습니다.',
					showConfirmButton: false,
					timer: 2000
				});
			}
		});
	});

	$("#delete-dept-btn").on("click", function () {

		const tree = $("#depTree").fancytree("getTree");
		const node = tree.getActiveNode();

		// 선택된 노드가 없거나, 사원 노드인 경우
		if (!node || node.data.empNm) {
			Swal.fire({
				toast: true,
				position: 'top',
				icon: 'warning',
				title: '삭제할 부서를 선택해주세요.',
				text: '사원이 아닌 부서를 선택해야 삭제할 수 있습니다.',
				showConfirmButton: false,
				timer: 2000
			});
			return;
		}

		const deptCd = node.key;
		const deptName = node.title;

		Swal.fire({
			title: `"${deptName}" 부서를 삭제하시겠습니까?`,
			text: "하위 부서나 사원이 있으면 삭제되지 않을 수 있습니다.",
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#d33',
			cancelButtonColor: '#3085d6',
			confirmButtonText: '삭제',
			cancelButtonText: '취소'
		}).then((result) => {
			if (!result.isConfirmed) return;

			$.ajax({
				type: "DELETE",
				url: `${contextPath}/department/delete/${deptCd}`,
				success: function () {
					Swal.fire({
						toast: true,
						position: 'top',
						icon: 'success',
						title: '삭제 완료',
						text: `"${deptName}" 부서가 삭제되었습니다.`,
						showConfirmButton: false,
						timer: 2000
					});

					tree.reload({
						url: `${contextPath}/organization/admin/parentDep`,
						cache: false
					});

					$("#detailContent").html(`<p>부서 또는 사원을 선택해주세요.</p>`);
				},
				error: function () {
					Swal.fire({
						toast: true,
						position: 'top',
						icon: 'error',
						title: '삭제 실패',
						text: `"${deptName}" 부서를 삭제할 수 없습니다.`,
						showConfirmButton: false,
						timer: 2000
					});
				}
			});
		});
	});

	$(document).on("click", "#bulkInsertBtn", function (e) {

		e.preventDefault();
		const targetUrl = $(this).attr("href");

		Swal.fire({
			title: '일괄등록 페이지로 이동하시겠습니까?',
			text: "진행 중인 정보는 저장되지 않습니다.",
			icon: 'question',
			showCancelButton: true,
			confirmButtonColor: '#2c7be5',
			cancelButtonColor: '#d33',
			confirmButtonText: '확인',
			cancelButtonText: '취소'
		}).then((result) => {
			if (result.isConfirmed) {
				location.href = targetUrl;
			}
		});
	});

	document.getElementById("mecro").addEventListener("click", function () {
		const autoFillData = {
			deptCd: "D012",
			deptName: "전략기획팀",
			parentDeptCd: "DP003"
		};

		document.getElementById("deptCd").value = autoFillData.deptCd;
		document.getElementById("deptName").value = autoFillData.deptName;
		document.getElementById("parentDeptCd").value = autoFillData.parentDeptCd;
	});

});

function renderTree(data) {
	$("#depTree").fancytree("getTree").reload(data);
}
