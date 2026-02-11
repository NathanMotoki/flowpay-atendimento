import { useState } from 'react';
import type { Atendimento } from '../types';
import { atendimentoService } from '../services/api';

interface AtendimentosEmAndamentoProps {
  atendimentos: Atendimento[];
  onFinalize: () => void;
}

export function AtendimentosEmAndamento({ atendimentos, onFinalize }: AtendimentosEmAndamentoProps) {
  const [finalizando, setFinalizando] = useState<number | null>(null);

  const filtrados = atendimentos.filter(a => a.status === 'EM_ATENDIMENTO');

  const handleFinalizar = async (id: number) => {
    setFinalizando(id);
    try {
      await atendimentoService.finalizar(id);
      onFinalize();
      alert('✅ Atendimento finalizado! Próximo da fila foi redistribuído.');
    } catch (error) {
      console.error('Erro ao finalizar:', error);
      alert('❌ Erro ao finalizar atendimento');
    } finally {
      setFinalizando(null);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'EM_ATENDIMENTO':
        return 'bg-blue-100 text-blue-800';
      case 'AGUARDANDO':
        return 'bg-yellow-100 text-yellow-800';
      case 'FINALIZADO':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTipoColor = (tipo: string) => {
    switch (tipo) {
      case 'CARTOES':
        return 'text-blue-600';
      case 'EMPRESTIMOS':
        return 'text-green-600';
      case 'OUTROS_ASSUNTOS':
        return 'text-purple-600';
      default:
        return 'text-gray-600';
    }
  };

  if (filtrados.length === 0) {
    return (
      <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100">
        <h3 className="text-2xl font-bold mb-6 text-gray-800">📞 Atendimentos em Andamento</h3>
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">Nenhum atendimento em andamento</p>
          <p className="text-gray-400 text-sm mt-2">Todos os clientes foram atendidos! 🎉</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100">
      <h3 className="text-2xl font-bold mb-6 text-gray-800">📞 Atendimentos em Andamento</h3>
      
      <div className="space-y-4">
        {filtrados.map((atendimento) => (
          <div
            key={atendimento.id}
            className="bg-white rounded-lg p-6 shadow hover:shadow-lg transition-all duration-200 border-l-4 border-l-blue-500"
          >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide font-semibold">Cliente</p>
                <p className="text-lg font-bold text-gray-800 mt-1">{atendimento.clienteNome}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide font-semibold">Atendente</p>
                <p className="text-lg font-bold text-gray-800 mt-1">{atendimento.atendenteNome || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide font-semibold">Assunto</p>
                <p className="text-sm text-gray-700 mt-1">{atendimento.assunto}</p>
              </div>
              <div className="flex items-end gap-2">
                <div>
                  <p className="text-xs text-gray-500 uppercase tracking-wide font-semibold">Time</p>
                  <p className={`text-sm font-bold mt-1 ${getTipoColor(atendimento.tipoTime)}`}>
                    {atendimento.tipoTime.replace(/_/g, ' ')}
                  </p>
                </div>
                <span className={`px-3 py-1 rounded-full text-xs font-bold ${getStatusColor(atendimento.status)}`}>
                  {atendimento.status.replace(/_/g, ' ')}
                </span>
              </div>
            </div>

            <div className="border-t pt-4 flex items-center justify-between">
              <p className="text-xs text-gray-500">
                ID: #{atendimento.id} • Criado em: {new Date(atendimento.dataHoraCriacao).toLocaleString('pt-BR')}
              </p>
              <button
                onClick={() => handleFinalizar(atendimento.id)}
                disabled={finalizando === atendimento.id}
                className="bg-gradient-to-r from-green-600 to-green-700 text-white px-4 py-1.5 rounded text-sm hover:from-green-700 hover:to-green-800 transition-all duration-200 font-semibold shadow-sm hover:shadow-lg active:scale-95 transform disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {finalizando === atendimento.id ? (
                  <>
                    <span className="animate-spin inline-block mr-2">⏳</span>
                    Finalizando...
                  </>
                ) : (
                  <>✅ Finalizar Atendimento</>
                )}
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-sm text-blue-900">
          <span className="font-bold">💡 Dica:</span> Ao finalizar um atendimento, o próximo da fila será automaticamente redistribuído para o atendente disponível.
        </p>
      </div>
    </div>
  );
}
