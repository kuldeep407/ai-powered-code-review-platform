import React from 'react';
import Button from './Button';
import { FiFolder } from 'react-icons/fi';

export const EmptyState = ({
  icon: Icon = FiFolder,
  title = 'No items found',
  description = 'There are no items to display at this time.',
  actionText,
  onAction,
  actionIcon,
}) => {
  return (
    <div className="flex flex-col items-center justify-center text-center py-12 px-4 rounded-xl border border-dashed border-slate-200 bg-white">
      <div className="p-3 bg-slate-50 border border-slate-100 rounded-full text-slate-400 mb-3">
        <Icon className="h-6 w-6" />
      </div>
      <h4 className="text-sm font-semibold text-slate-800">{title}</h4>
      <p className="mt-1 text-xs text-slate-500 max-w-sm">{description}</p>
      {actionText && onAction && (
        <div className="mt-5">
          <Button onClick={onAction} size="sm" icon={actionIcon}>
            {actionText}
          </Button>
        </div>
      )}
    </div>
  );
};

export default EmptyState;
