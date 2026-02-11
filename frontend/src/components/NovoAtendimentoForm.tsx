import { useState } from 'react';
import type { NovoAtendimentoRequest } from '../types';

interface NovoAtendimentoFormProps {
  onSubmit: (data: NovoAtendimentoRequest) => void;
}

export function NovoAtendimentoForm({ onSubmit }: NovoAtendimentoFormProps) {
  const [clienteNome, setClienteNome] = useState('');
  const [assunto, setAssunto] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ clienteNome, assunto });
    setClienteNome('');
    setAssunto('');
  };

  return (
    <form onSubmit={handleSubmit} className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-7 border border-gray-100 hover:shadow-xl transition-shadow duration-300">
      <div className="mb-6">
        <h3 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent">📝 Novo Atendimento</h3>
        <p className="text-sm text-gray-500 mt-1">Crie uma nova solicitação de atendimento</p>
      </div>
      
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            📧 Nome do Cliente
          </label>
          <input
            type="text"
            value={clienteNome}
            onChange={(e) => setClienteNome(e.target.value)}
            className="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white transition-all duration-200 hover:border-gray-300"
            placeholder="Digite o nome completo"
            required
            minLength={3}
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            🎯 Assunto
          </label>
          <select
            value={assunto}
            onChange={(e) => setAssunto(e.target.value)}
            className="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white transition-all duration-200 hover:border-gray-300 cursor-pointer"
            required
          >
            <option value="">Selecione o assunto</option>
            <option value="Problemas com cartão">💳 Problemas com cartão</option>
            <option value="Empréstimo">💰 Empréstimo</option>
            <option value="Outros">📋 Outros</option>
          </select>
        </div>

        <button
          type="submit"
          className="w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white py-2 px-4 rounded-lg hover:from-blue-700 hover:to-blue-800 transition-all duration-200 font-semibold shadow-md hover:shadow-lg active:scale-95 transform text-sm"
        >
          ✨ Criar Atendimento
        </button>
      </div>
    </form>
  );
}