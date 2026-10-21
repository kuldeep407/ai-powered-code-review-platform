import React from 'react';
import Badge from './Badge';
import { FiGitPullRequest, FiCheckCircle, FiXCircle, FiClock } from 'react-icons/fi';

export const StatusBadge = ({ status }) => {
  const norm = (status || '').toUpperCase();

  switch (norm) {
    case 'OPEN':
      return (
        <Badge variant="success" icon={FiGitPullRequest}>
          Open
        </Badge>
      );
    case 'MERGED':
      return (
        <Badge variant="purple" icon={FiCheckCircle}>
          Merged
        </Badge>
      );
    case 'CLOSED':
      return (
        <Badge variant="danger" icon={FiXCircle}>
          Closed
        </Badge>
      );
    case 'COMPLETED':
      return (
        <Badge variant="success" icon={FiCheckCircle}>
          Completed
        </Badge>
      );
    case 'PENDING':
      return (
        <Badge variant="warning" icon={FiClock}>
          Pending
        </Badge>
      );
    case 'FAILED':
      return (
        <Badge variant="danger" icon={FiXCircle}>
          Failed
        </Badge>
      );
    default:
      return <Badge variant="default">{status}</Badge>;
  }
};

export default StatusBadge;
