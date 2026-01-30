/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 3. 22.     	JSW            최초 생성
 *
 * </pre>
 */
document.addEventListener("DOMContentLoaded", function () {
   // 현재 페이지 URL 가져오기
   var currentPath = window.location.pathname;

   // 모든 드롭다운 메뉴 요소를 가져오기
   var dropdownMenus = document.querySelectorAll(".collapse");

   dropdownMenus.forEach(function (menu) {
     var menuLinks = menu.querySelectorAll("a");

     menuLinks.forEach(function (link) {
       if (link.getAttribute("href") === currentPath) {
         // 해당 메뉴가 현재 페이지라면 활성화 상태 유지
         menu.classList.add("show");

         // 부모 <li> 요소도 활성화 표시
         var parentNavItem = menu.closest(".nav-item");
         if (parentNavItem) {
           parentNavItem.classList.add("active");
         }
       }
     });
   });
 });