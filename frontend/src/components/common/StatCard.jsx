import React from 'react';

export const StatCard = ({
  title,
  value,
  icon: Icon,
  trend,
  trendPositive = true,
  subtitle,
  iconColor = 'text-indigo-600 bg-indigo-50 border border-indigo-100',
  className = '',
}) => {
  return (
    <div className={`bg-white border border-slate-200 rounded-xl p-5 shadow-xs flex flex-col justify-between ${className}`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">{title}</p>
          <h4 className="text-2xl font-bold text-slate-900 mt-2">{value}</h4>
        </div>
        {Icon && (
          <div className={`p-3 rounded-lg ${iconColor}`}>
            <Icon className="h-5 w-5" />
          </div>
        )}
      </div>
      {(trend || subtitle) && (
        <div className="mt-4 flex items-center text-xs text-slate-500 pt-3 border-t border-slate-100">
          {trend && (
            <span className={`font-semibold mr-1.5 ${trendPositive ? 'text-emerald-600' : 'text-rose-600'}`}>
              {trend}
            </span>
          )}
          <span>{subtitle}</span>
        </div>
      )}
    </div>
  );
};

export default StatCard;
