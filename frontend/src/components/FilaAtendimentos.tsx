import type { Atendimento } from '../types';

interface FilaAtendimentosProps {
  atendimentos: Atendimento[];
}

export function FilaAtendimentos({ atendimentos }: FilaAtendimentosProps) {
  const filtrados = atendimentos.filter(a => a.status === 'AGUARDANDO');

  const getTipoColor = (tipo: string) => {
    switch (tipo) {
      case 'CARTOES':
        return 'bg-blue-100 border-blue-300 text-blue-700';
      case 'EMPRESTIMOS':
        return 'bg-green-100 border-green-300 text-green-700';
      case 'OUTROS_ASSUNTOS':
        return 'bg-purple-100 border-purple-300 text-purple-700';
      default:
        return 'bg-gray-100 border-gray-300 text-gray-700';
    }
  };

  if (filtrados.length === 0) {
    return (
      <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100">
        <h3 className="text-2xl font-bold mb-6 text-gray-800">⏳ Fila de Atendimentos</h3>
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">Fila vazia!</p>
          <p className="text-gray-400 text-sm mt-2">Todos os clientes estão sendo atendidos 🚀</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100">
      <h3 className="text-2xl font-bold mb-6 text-gray-800">⏳ Fila de Atendimentos ({filtrados.length})</h3>
      
      <div className="space-y-3">
        {filtrados.map((atendimento, index) => (
          <div
            key={atendimento.id}
            className={`bg-white rounded-lg p-4 shadow hover:shadow-md transition-all duration-200 border-l-4 border-l-yellow-500 ${
              index === 0 ? 'ring-2 ring-yellow-300' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-3 mb-2">
                  <div className="bg-yellow-100 text-yellow-800 font-bold rounded-full w-8 h-8 flex items-center justify-center text-sm">
                    #{index + 1}
                  </div>
                  <div>
                    <p className="font-semibold text-gray-800">{atendimento.clienteNome}</p>
                    <p className="text-sm text-gray-600">{atendimento.assunto}</p>
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <span className={`px-3 py-1 rounded-full text-xs font-bold border ${getTipoColor(atendimento.tipoTime)}`}>
                  {atendimento.tipoTime.replace(/_/g, ' ')}
                </span>
                {index === 0 && (
                  <span className="animate-pulse">
                    <span className="inline-block px-3 py-1 bg-green-100 text-green-800 rounded-full text-xs font-bold">
                      🟢 Próximo
                    </span>
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-amber-50 border border-amber-200 rounded-lg">
        <p className="text-sm text-amber-900">
          <span className="font-bold">⏳ Atenção:</span> {filtrados.length} cliente(s) aguardando atendimento. Quando um atendimento for finalizado, o próximo será automaticamente distribuído.
        </p>
      </div>
    </div>
  );
}
