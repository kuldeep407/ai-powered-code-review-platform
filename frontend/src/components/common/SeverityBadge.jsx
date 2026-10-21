import React from 'react';
import Badge from './Badge';
import { FiAlertTriangle, FiAlertCircle, FiInfo, FiCheckCircle } from 'react-icons/fi';

export const SeverityBadge = ({ severity }) => {
  const norm = (severity || '').toUpperCase();

  switch (norm) {
    case 'CRITICAL':
      return (
        <Badge variant="danger" icon={FiAlertCircle} className="font-semibold">
          CRITICAL
        </Badge>
      );
    case 'HIGH':
      return (
        <Badge variant="danger" icon={FiAlertTriangle}>
          HIGH
        </Badge>
      );
    case 'MEDIUM':
      return (
        <Badge variant="warning" icon={FiAlertTriangle}>
          MEDIUM
        </Badge>
      );
    case 'LOW':
    default:
      return (
        <Badge variant="sky" icon={FiInfo}>
          LOW
        </Badge>
      );
  }
};

export default SeverityBadge;
