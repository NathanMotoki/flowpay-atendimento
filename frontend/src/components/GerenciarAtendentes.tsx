import { useState } from 'react';
import type { Atendente, TipoTime } from '../types';
import { atendenteService } from '../services/api';

interface GerenciarAtendentesProps {
  atendentes: Atendente[];
  onRefresh: () => void;
}

export function GerenciarAtendentes({ atendentes, onRefresh }: GerenciarAtendentesProps) {
  const [nome, setNome] = useState('');
  const [tipoTime, setTipoTime] = useState<TipoTime>('CARTOES');
  const [criando, setCriando] = useState(false);
  const [deletando, setDeletando] = useState<number | null>(null);

  const handleCriar = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nome.trim()) return;

    setCriando(true);
    try {
      await atendenteService.criar({ nome, tipoTime });
      setNome('');
      setTipoTime('CARTOES');
      onRefresh();
      alert('✅ Atendente criado com sucesso!');
    } catch (error) {
      console.error('Erro ao criar atendente:', error);
      alert('❌ Erro ao criar atendente');
    } finally {
      setCriando(false);
    }
  };

  const handleDeletar = async (id: number) => {
    if (!confirm('Tem certeza que deseja remover este atendente?')) return;

    setDeletando(id);
    try {
      await atendenteService.deletar(id);
      onRefresh();
      alert('✅ Atendente removido com sucesso!');
    } catch (error) {
      console.error('Erro ao deletar atendente:', error);
      alert('❌ Erro ao remover atendente');
    } finally {
      setDeletando(null);
    }
  };

  const getTipoColor = (tipo: TipoTime) => {
    switch (tipo) {
      case 'CARTOES':
        return 'bg-blue-100 text-blue-700 border-blue-300';
      case 'EMPRESTIMOS':
        return 'bg-green-100 text-green-700 border-green-300';
      case 'OUTROS_ASSUNTOS':
        return 'bg-purple-100 text-purple-700 border-purple-300';
      default:
        return 'bg-gray-100 text-gray-700 border-gray-300';
    }
  };

  const getStatusColor = (disponivel: boolean) => {
    return disponivel ? 'text-green-600 font-bold' : 'text-orange-600 font-bold';
  };

  return (
    <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100 h-fit">
      <h3 className="text-2xl font-bold mb-6 text-gray-800">👥 Gerenciar Atendentes</h3>

      {/* Formulário de Criação */}
      <form onSubmit={handleCriar} className="mb-8 pb-8 border-b border-gray-200">
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              📝 Nome do Atendente
            </label>
            <input
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              className="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white transition-all duration-200 hover:border-gray-300"
              placeholder="Digite o nome"
              required
              minLength={3}
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              🎯 Time
            </label>
            <select
              value={tipoTime}
              onChange={(e) => setTipoTime(e.target.value as TipoTime)}
              className="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white transition-all duration-200 hover:border-gray-300 cursor-pointer"
            >
              <option value="CARTOES">💳 Cartões</option>
              <option value="EMPRESTIMOS">💰 Empréstimos</option>
              <option value="OUTROS_ASSUNTOS">📋 Outros Assuntos</option>
            </select>
          </div>

          <button
            type="submit"
            disabled={criando}
            className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-2 px-4 rounded-lg hover:from-green-700 hover:to-green-800 transition-all duration-200 font-semibold shadow-md hover:shadow-lg active:scale-95 transform disabled:opacity-50 disabled:cursor-not-allowed text-sm"
          >
            {criando ? (
              <>
                <span className="animate-spin inline-block mr-1">⏳</span>
                Criando...
              </>
            ) : (
              <>✨ Novo Atendente</>
            )}
          </button>
        </div>
      </form>

      {/* Lista de Atendentes */}
      <div>
        <h4 className="font-bold text-lg mb-4 text-gray-800">Lista de Atendentes ({atendentes.length})</h4>
        <div className="space-y-3 max-h-96 overflow-y-auto">
          {atendentes.length === 0 ? (
            <p className="text-gray-500 text-center py-8 italic">Nenhum atendente cadastrado</p>
          ) : (
            atendentes.map((atendente) => (
              <div
                key={atendente.id}
                className="bg-white rounded-lg p-4 shadow hover:shadow-md transition-all duration-200 border-l-4 border-l-gray-300 hover:border-l-blue-500"
              >
                <div className="flex items-center justify-between mb-2">
                  <div className="flex-1">
                    <p className="font-semibold text-gray-800">{atendente.nome}</p>
                    <div className="flex items-center gap-2 mt-1">
                      <span className={`px-2 py-1 rounded-full text-xs font-bold border ${getTipoColor(atendente.tipoTime)}`}>
                        {atendente.tipoTime.replace(/_/g, ' ')}
                      </span>
                      <span className={`text-xs font-semibold ${getStatusColor(atendente.disponivel)}`}>
                        {atendente.disponivel ? '🟢 Disponível' : '🟠 Ocupado'} • {atendente.atendimentosAtuais}/3
                      </span>
                    </div>
                  </div>
                  <button
                    onClick={() => handleDeletar(atendente.id)}
                    disabled={deletando === atendente.id}
                    className="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded text-sm transition-all duration-200 font-semibold shadow-sm hover:shadow-lg active:scale-95 transform disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {deletando === atendente.id ? '⏳' : '🗑️'}
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
