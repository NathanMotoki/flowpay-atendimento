interface MetricsCardProps {
  title: string;
  value: number;
  icon: string;
  color: string;
}

export function MetricsCard({ title, value, icon, color }: MetricsCardProps) {
  return (
    <div className="bg-gradient-to-br from-white to-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 p-6 border border-gray-100 hover:scale-105 transform">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-gray-600 text-sm font-semibold uppercase tracking-wide">{title}</p>
          <p className={`text-4xl font-bold mt-3 ${color}`}>{value}</p>
        </div>
        <div className={`text-5xl opacity-80`}>{icon}</div>
      </div>
    </div>
  );
}