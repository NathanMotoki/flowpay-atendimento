import { type Atendente, type TipoTime } from '../types';

interface AtendentesListProps {
  atendentes: Atendente[];
  tipoTime: TipoTime;
}

const TIME_LABELS: Record<TipoTime, string> = {
  CARTOES: 'Time Cartões',
  EMPRESTIMOS: 'Time Empréstimos',
  OUTROS_ASSUNTOS: 'Time Outros Assuntos',
};

const TIME_COLORS: Record<TipoTime, string> = {
  CARTOES: 'bg-gradient-to-br from-blue-50 to-blue-100 border-blue-300',
  EMPRESTIMOS: 'bg-gradient-to-br from-green-50 to-green-100 border-green-300',
  OUTROS_ASSUNTOS: 'bg-gradient-to-br from-purple-50 to-purple-100 border-purple-300',
};

const TIME_ACCENT_COLORS: Record<TipoTime, string> = {
  CARTOES: 'text-blue-700 bg-blue-100',
  EMPRESTIMOS: 'text-green-700 bg-green-100',
  OUTROS_ASSUNTOS: 'text-purple-700 bg-purple-100',
};

const TIME_BADGE_COLORS: Record<TipoTime, string> = {
  CARTOES: 'bg-blue-500',
  EMPRESTIMOS: 'bg-green-500',
  OUTROS_ASSUNTOS: 'bg-purple-500',
};

export function AtendentesList({ atendentes, tipoTime }: AtendentesListProps) {
  const atendentesDoTime = atendentes.filter(a => a.tipoTime === tipoTime);

  return (
    <div className={`rounded-xl border-2 p-6 ${TIME_COLORS[tipoTime]} shadow-md hover:shadow-lg transition-shadow duration-300`}>
      <h3 className={`text-lg font-bold mb-4 ${TIME_ACCENT_COLORS[tipoTime]} px-3 py-2 rounded-lg inline-block`}>
        {TIME_LABELS[tipoTime]}
      </h3>
      
      <div className="space-y-3 mt-4">
        {atendentesDoTime.length === 0 ? (
          <p className="text-gray-500 text-center py-4 italic">Nenhum atendente neste time</p>
        ) : (
          atendentesDoTime.map((atendente) => (
            <div
              key={atendente.id}
              className="bg-white rounded-lg p-4 shadow hover:shadow-md transition-all duration-200 border-l-4 border-l-gray-300 hover:border-l-blue-500"
            >
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-800">{atendente.nome}</span>
                <div className="flex items-center gap-3">
                  <span className={`text-xs font-bold px-2 py-1 rounded-full ${TIME_ACCENT_COLORS[tipoTime]}`}>
                    {atendente.atendimentosAtuais}/3
                  </span>
                  <div className="flex gap-1.5">
                    {[1, 2, 3].map((i) => (
                      <div
                        key={i}
                        className={`w-2.5 h-2.5 rounded-full transition-all ${
                          i <= atendente.atendimentosAtuais
                            ? `${TIME_BADGE_COLORS[tipoTime]}`
                            : 'bg-gray-300'
                        }`}
                      />
                    ))}
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}