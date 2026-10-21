import React from 'react';
import Button from './Button';
import { FiAlertCircle, FiRefreshCw } from 'react-icons/fi';

export const ErrorState = ({
  title = 'Something went wrong',
  message = 'Failed to load data from server. Please check your connection and try again.',
  onRetry,
}) => {
  return (
    <div className="flex flex-col items-center justify-center text-center py-10 px-4 rounded-xl border border-rose-200 bg-rose-50/40">
      <div className="p-3 bg-rose-100/70 text-rose-600 rounded-full mb-3">
        <FiAlertCircle className="h-6 w-6" />
      </div>
      <h4 className="text-sm font-semibold text-rose-900">{title}</h4>
      <p className="mt-1 text-xs text-rose-700 max-w-sm">{message}</p>
      {onRetry && (
        <div className="mt-4">
          <Button onClick={onRetry} variant="outline" size="sm" icon={FiRefreshCw}>
            Try Again
          </Button>
        </div>
      )}
    </div>
  );
};

export default ErrorState;
