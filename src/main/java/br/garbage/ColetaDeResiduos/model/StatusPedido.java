package br.garbage.ColetaDeResiduos.model;

public enum StatusPedido {
    PENDENTE("Pendente"),
    AGENDADO("Agendado"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDO("Concluída"),
    CANCELADO("Cancelada");

    private final String rotulo;

    StatusPedido(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public boolean podeTransitarPara(StatusPedido novo) {
        if (novo == null) return false;
        if (this == novo) return true;
        return switch (this) {
            case PENDENTE -> true;
            case AGENDADO -> novo == EM_EXECUCAO || novo == CONCLUIDO || novo == CANCELADO;
            case EM_EXECUCAO -> novo == CONCLUIDO || novo == CANCELADO;
            case CONCLUIDO, CANCELADO -> false;
        };
    }

    public static StatusPedido fromTextoLegado(String texto) {
        if (texto == null) return PENDENTE;
        String t = texto.trim().toUpperCase();
        if (t.startsWith("CONCLU")) return CONCLUIDO;
        if (t.startsWith("CANCEL")) return CANCELADO;
        if (t.startsWith("AGEND")) return AGENDADO;
        if (t.startsWith("EXEC")) return EM_EXECUCAO;
        return PENDENTE;
    }
}
