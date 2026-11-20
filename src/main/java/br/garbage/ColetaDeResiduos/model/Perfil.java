package br.garbage.ColetaDeResiduos.model;

public enum Perfil {
    ADMIN(1, "Administrativo"),
    OPERADOR(2, "Operador Ambiental"),
    CLIENTE(3, "Cliente Gerador");

    private final int codigo;
    private final String descricao;

    Perfil(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
}
