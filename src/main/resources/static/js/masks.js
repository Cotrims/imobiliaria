document.addEventListener("DOMContentLoaded", function () {
  if (typeof $ === "undefined") {
    console.error("jQuery não foi carregado.");
    return;
  }

  if (typeof $.fn.mask === "undefined") {
    console.error("jquery-mask-plugin não foi carregado.");
    return;
  }

  $("#cpf").mask("000.000.000-00");

  $("#telefone").mask("(00) 00000-0000");

  $("#CNPJ").mask("00.000.000/0000-00");
});
