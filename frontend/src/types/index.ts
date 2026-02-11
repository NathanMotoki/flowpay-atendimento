export enum TipoTime {
  CARTOES = 'CARTOES',
  EMPRESTIMOS = 'EMPRESTIMOS',
  OUTROS_ASSUNTOS = 'OUTROS_ASSUNTOS'
}

export enum StatusAtendimento {
  AGUARDANDO = 'AGUARDANDO',
  EM_ATENDIMENTO = 'EM_ATENDIMENTO',
  FINALIZADO = 'FINALIZADO'
}

export interface Atendimento {
  id: number;
  clienteNome: string;
  assunto: string;
  tipoTime: TipoTime;
  status: StatusAtendimento;
  atendenteId: number | null;
  atendenteNome: string | null;
  dataHoraInicio: string | null;
  dataHoraFim: string | null;
  dataHoraCriacao: string;
}

export interface Atendente {
  id: number;
  nome: string;
  tipoTime: TipoTime;
  atendimentosAtuais: number;
  disponivel: boolean;
}

export interface TimeMetrics {
  tipo: TipoTime;
  atendimentosAtivos: number;
  tamanhoFila: number;
  atendentesDisponiveis: number;
  atendentesOcupados: number;
}

export interface DashboardMetrics {
  totalAtendimentosEmAndamento: number;
  totalNaFila: number;
  atendentesDisponiveis: number;
  atendentesOcupados: number;
  metricasPorTime: Record<TipoTime, TimeMetrics>;
}

export interface NovoAtendimentoRequest {
  clienteNome: string;
  assunto: string;
}