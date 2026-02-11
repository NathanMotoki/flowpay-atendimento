import { useEffect, useState } from 'react';
import { MetricsCard } from './components/MetricsCard';
import { AtendentesList } from './components/AtendentesList';
import { NovoAtendimentoForm } from './components/NovoAtendimentoForm';
import { AtendimentosEmAndamento } from './components/AtendimentosEmAndamento';
import { FilaAtendimentos } from './components/FilaAtendimentos';
import { GerenciarAtendentes } from './components/GerenciarAtendentes';
import { dashboardService, atendenteService, atendimentoService } from './services/api';
import type { DashboardMetrics, Atendente, Atendimento, NovoAtendimentoRequest, TipoTime } from './types';

type ActiveTab = 'dashboard' | 'atendimento' | 'gerenciar';

interface NavItem {
  id: ActiveTab;
  label: string;
  icon: string;
  description: string;
}

const navItems: NavItem[] = [
  { id: 'dashboard', label: 'Dashboard', icon: '📊', description: 'Visão geral do sistema' },
  { id: 'atendimento', label: 'Novo Atendimento', icon: '📞', description: 'Criar novo atendimento' },
  { id: 'gerenciar', label: 'Gerenciar Atendentes', icon: '👥', description: 'Criar ou remover atendentes' },
];

function App() {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [atendentes, setAtendentes] = useState<Atendente[]>([]);
  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<ActiveTab>('dashboard');

  const loadData = async () => {
    try {
      const [metricsRes, atendentesRes, atendimentosRes] = await Promise.all([
        dashboardService.getMetricas(),
        atendenteService.listarTodos(),
        atendimentoService.listarTodos(),
      ]);
      setMetrics(metricsRes.data);
      setAtendentes(atendentesRes.data);
      setAtendimentos(atendimentosRes.data);
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 5000); // Atualiza a cada 5 segundos
    return () => clearInterval(interval);
  }, []);

  const handleNovoAtendimento = async (data: NovoAtendimentoRequest) => {
    try {
      await atendimentoService.criar(data);
      loadData(); // Recarrega os dados
      alert('✅ Atendimento criado com sucesso!');
    } catch (error) {
      console.error('Erro ao criar atendimento:', error);
      alert('❌ Erro ao criar atendimento');
    }
  };

  const renderContent = () => {
    if (loading) {
      return (
        <div className="bg-white rounded-xl shadow-lg p-12 text-center">
          <div className="text-5xl mb-4">⏳</div>
          <p className="text-lg font-semibold text-gray-600">Carregando dados...</p>
        </div>
      );
    }

    if (!metrics) {
      return (
        <div className="bg-white rounded-xl shadow-lg p-12 text-center">
          <div className="text-5xl mb-4">❌</div>
          <p className="text-lg font-semibold text-gray-600">Erro ao carregar dados. Tente recarregar a página.</p>
        </div>
      );
    }

    if (activeTab === 'dashboard') {
      return (
        <div className="space-y-8">
          {/* Métricas Gerais - Grid animado */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
              <span className="text-3xl">📊</span> Panorama Geral
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
              <MetricsCard
                title="Em Atendimento"
                value={metrics.totalAtendimentosEmAndamento}
                icon="🔵"
                color="text-blue-600"
              />
              <MetricsCard
                title="Na Fila"
                value={metrics.totalNaFila}
                icon="⏳"
                color="text-amber-500"
              />
              <MetricsCard
                title="Disponíveis"
                value={metrics.atendentesDisponiveis}
                icon="✅"
                color="text-emerald-600"
              />
              <MetricsCard
                title="Ocupados"
                value={metrics.atendentesOcupados}
                icon="🔴"
                color="text-red-600"
              />
            </div>
          </div>

          {/* Status dos Times - Cards lado a lado */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
              <span className="text-3xl">🎯</span> Status dos Times
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="md:col-span-1">
                <AtendentesList atendentes={atendentes} tipoTime={'CARTOES' as TipoTime} />
              </div>
              <div className="md:col-span-1">
                <AtendentesList atendentes={atendentes} tipoTime={'EMPRESTIMOS' as TipoTime} />
              </div>
              <div className="md:col-span-1">
                <AtendentesList atendentes={atendentes} tipoTime={'OUTROS_ASSUNTOS' as TipoTime} />
              </div>
            </div>
          </div>

          {/* Métricas Detalhadas por Time */}
          <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg p-8 border border-gray-100">
            <h3 className="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
              <span className="text-3xl">📈</span> Análise Detalhada por Time
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
              {Object.entries(metrics.metricasPorTime).map(([tipo, data]) => renderTimeMetric(tipo, data))}
            </div>
          </div>

          {/* Seção Principal: Atendimentos e Fila */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
              <span className="text-3xl">⚡</span> Atendimentos em Tempo Real
            </h2>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <AtendimentosEmAndamento atendimentos={atendimentos} onFinalize={loadData} />
              <FilaAtendimentos atendimentos={atendimentos} />
            </div>
          </div>
        </div>
      );
    }

    if (activeTab === 'atendimento') {
      return (
        <div className="max-w-2xl mx-auto">
          <NovoAtendimentoForm onSubmit={handleNovoAtendimento} />
        </div>
      );
    }

    if (activeTab === 'gerenciar') {
      return (
        <div className="max-w-3xl mx-auto">
          <GerenciarAtendentes atendentes={atendentes} onRefresh={loadData} />
        </div>
      );
    }

    return null;
  };

  const renderTimeMetric = (tipo: string, data: any) => {
    const tipoFormatado = tipo.replaceAll('_', ' ');
    const corFundo =
      tipo === 'CARTOES'
        ? 'bg-blue-50 border-blue-200 hover:border-blue-400'
        : tipo === 'EMPRESTIMOS'
          ? 'bg-green-50 border-green-200 hover:border-green-400'
          : 'bg-purple-50 border-purple-200 hover:border-purple-400';

    return (
      <div key={tipo} className={`border-2 rounded-lg p-6 transition-all duration-200 hover:shadow-lg cursor-pointer transform hover:scale-105 ${corFundo}`}>
        <h4 className="font-bold mb-4 text-gray-800 text-lg">{tipoFormatado}</h4>
        <div className="space-y-3 text-sm">
          <div className="flex items-center gap-2 bg-white/70 p-2 rounded-lg">
            <span className="text-lg">🔄</span>
            <span className="text-gray-700">
              Ativos: <span className="font-bold text-lg">{data.atendimentosAtivos}</span>
            </span>
          </div>
          <div className="flex items-center gap-2 bg-white/70 p-2 rounded-lg">
            <span className="text-lg">⏱️</span>
            <span className="text-gray-700">
              Fila: <span className="font-bold text-lg">{data.tamanhoFila}</span>
            </span>
          </div>
          <div className="flex items-center gap-2 bg-white/70 p-2 rounded-lg">
            <span className="text-lg">👥</span>
            <span className="text-gray-700">
              Disponíveis:{' '}
              <span className="font-bold text-lg">
                {data.atendentesDisponiveis}/{data.atendentesDisponiveis + data.atendentesOcupados}
              </span>
            </span>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Header Premium */}
      <div className="bg-gradient-to-r from-blue-600 via-blue-700 to-blue-800 text-white shadow-lg sticky top-0 z-40 border-b-2 border-blue-400">
        <div className="max-w-7xl mx-auto px-6 md:px-8 py-3">
          <div className="flex items-center gap-2">
            <span className="text-3xl drop-shadow-lg">🏦</span>
            <div>
              <h1 className="text-2xl md:text-3xl font-bold drop-shadow-lg">FlowPay</h1>
              <p className="text-blue-100 text-xs font-light">Gestão de Atendimentos</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-6 md:px-8 py-6">
        {/* Navegação Principal - Cards com Destaque */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3 mb-8">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`group relative overflow-hidden rounded-lg p-4 shadow transition-all duration-300 transform hover:scale-102 ${
                activeTab === item.id
                  ? 'bg-gradient-to-br from-blue-500 to-blue-700 text-white shadow-lg ring-2 ring-blue-300 scale-102'
                  : 'bg-gradient-to-br from-white to-gray-50 text-gray-800 hover:shadow-md hover:from-blue-50 hover:to-gray-100'
              }`}
            >
              <div className="absolute inset-0 bg-gradient-to-r from-transparent to-white opacity-0 group-hover:opacity-10 transition-opacity duration-300" />
              <div className="relative z-10">
                <div className="text-3xl mb-1 transform group-hover:scale-110 transition-transform duration-300">{item.icon}</div>
                <h3 className="text-base font-bold mb-0.5">{item.label}</h3>
                <p className={`text-xs transition-colors duration-300 ${activeTab === item.id ? 'text-blue-100' : 'text-gray-600'}`}>
                  {item.description}
                </p>
              </div>
            </button>
          ))}
        </div>

        {/* Conteúdo Principal com Animação */}
        <div className="mb-8 animate-fadeIn">{renderContent()}</div>
      </div>

      <style>{`
        @keyframes fadeIn {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
        .animate-fadeIn {
          animation: fadeIn 0.5s ease-out;
        }
      `}</style>
    </div>
  );
}

export default App;