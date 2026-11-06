document.addEventListener("click", function (event) {
  var button = event.target.closest("[data-confirm]");
  if (button && !window.confirm(button.getAttribute("data-confirm"))) event.preventDefault();
});

document.addEventListener("DOMContentLoaded", function () {
  var periodSelect = document.querySelector("#report-period");
  if (!periodSelect) return;

  var periodFields = document.querySelectorAll("[data-report-period]");
  var updatePeriodFields = function () {
    periodFields.forEach(function (field) {
      field.hidden = field.dataset.reportPeriod !== periodSelect.value;
      field.querySelectorAll("input, select").forEach(function (control) {
        control.disabled = field.hidden;
      });
    });
  };

  periodSelect.addEventListener("change", updatePeriodFields);
  updatePeriodFields();
});
