$(document).ready(function () {

    const table = $("#multi-filter-select").DataTable({
        serverSide: true,
        processing: true,
        dom: 'lfrtip',
        ajax: {
            url: contextPath + "/employee/admin/ajaxList",
            type: "GET",
            data: function(d) {
                d.searchType = $('select[name="searchType"]').val();
                d.searchWord = $('#custom-search-input').val();
            }
        },
        columns: [
            {
                data: 'empId',
                render: data => `<input type="checkbox" class="row-checkbox" name="empCheck" value="${data}"/>`,
                orderable: false,
                searchable: false
            },
            { data: 'empNo' },
            { data: 'empNm' },
            { data: 'deptName' },
            { data: 'positionName' },
            { data: 'empEmail' }
        ],
        lengthMenu: [10,15,20,25],
        info: false,
        language: {
            lengthMenu: "_MENU_ 개씩 보기",
            zeroRecords: "일치하는 사원이 없습니다.",
            info: "_TOTAL_명 중 _START_부터 _END_까지 표시",
            infoEmpty: "사원이 없습니다.",
            infoFiltered: "(전체 _MAX_명 중 필터링됨)",
            search: "검색:",
            paginate: { previous: "이전", next: "다음" },
            processing: "로딩 중..."
        }
    });

    $('.dataTables_filter').html(`
      <select name="searchType" class="form-select form-select-sm" style="width:120px;">
        <option value="empNm">이름</option>
        <option value="deptName">부서</option>
        <option value="positionName">직책</option>
      </select>
      <input type="text" id="custom-search-input" class="form-control form-control-sm" style="width:150px;">
      <button class="btn btn-primary btn-sm" id="dt-search-btn">검색</button>
    `);

    $(document).on('click','#dt-search-btn',()=>table.ajax.reload());
    $(document).on('keydown','#custom-search-input',e=>{if(e.key==='Enter'){$('#dt-search-btn').click();}});

    // 전체 체크
    $(document).on('change','#checkAll',function(){
        $('.row-checkbox').prop('checked',this.checked);
    });

    // 부서 불러오기
    $('#dept-select-wrap').on('click','#loadDeptBtn',function(){
        $('#loadDeptBtn').hide();
        if($('select[name="deptCd"]').length>0) return;

        const $deptSelect = $('<select name="deptCd" class="form-select mt-2"><option value="">부서 선택</option></select>');

        $.get(contextPath + "/employee/departments",function(list){
            list.forEach(d=> $deptSelect.append(`<option value="${d.deptCd}">${d.deptName}</option>`));
            $('#dept-select-wrap').append($deptSelect);
        });
    });

    // 사원 등록
    $('#employeeForm').on('submit',function(e){
        e.preventDefault();
        const formData = new FormData(this);

        $.ajax({
            url: contextPath + "/employee/admin/new",
            type:'POST',
            data: formData,
            processData:false,
            contentType:false,
            success:()=>{ Swal.fire({icon:'success',title:'등록 완료'}); location.reload(); }
        });
    });

    // 엑셀 다운로드
    $('#downExcel').on('click',function(e){
        e.preventDefault();
        const q = new URLSearchParams({
            searchType:$('select[name="searchType"]').val()||'',
            searchWord:$('#custom-search-input').val()||''
        });
        window.location.href = contextPath + "/employee/admin/excelDownload?" + q;
    });

    // 일괄 수정
    $('#confirmBulkUpdate').on('click',function(){
        const empIds = $('.row-checkbox:checked').map((_,e)=>e.value).get();
        $.ajax({
            url: contextPath + "/employee/admin/bulkUpdate",
            method:'PUT',
            contentType:'application/json',
            data: JSON.stringify({empIds, fieldType:'position', value:$('#bulkValue').val()}),
            success:()=>{ Swal.fire({icon:'success',title:'수정 완료'}); table.ajax.reload(); }
        });
    });

    // 삭제
    $('#bulkDelete').on('click',function(){
        const empIds = $('.row-checkbox:checked').map((_,e)=>e.value).get();
        $.ajax({
            url: contextPath + "/employee/admin/delete",
            method:'DELETE',
            contentType:'application/json',
            data: JSON.stringify(empIds),
            success:()=>{ Swal.fire({icon:'success',title:'삭제 완료'}); table.ajax.reload(); }
        });
    });

});
