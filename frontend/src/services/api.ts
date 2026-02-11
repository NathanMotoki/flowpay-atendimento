import axios from 'axios';
import type { Atendimento, Atendente, DashboardMetrics, NovoAtendimentoRequest, TipoTime } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const atendimentoService = {
  listarTodos: () => api.get<Atendimento[]>('/atendimentos'),
  criar: (data: NovoAtendimentoRequest) => api.post<Atendimento>('/atendimentos', data),
  finalizar: (id: number) => api.put<Atendimento>(`/atendimentos/${id}/finalizar`),
};

export const atendenteService = {
  listarTodos: () => api.get<Atendente[]>('/atendentes'),
  criar: (data: { nome: string; tipoTime: TipoTime }) => api.post<Atendente>('/atendentes', data),
  deletar: (id: number) => api.delete(`/atendentes/${id}`),
};

export const dashboardService = {
  getMetricas: () => api.get<DashboardMetrics>('/dashboard/metricas'),
};