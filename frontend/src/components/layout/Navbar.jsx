import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { FiMenu, FiGithub, FiCheck, FiCpu, FiExternalLink } from 'react-icons/fi';
import Badge from '../common/Badge';

export const Navbar = ({ onOpenSidebar, onOpenGithubModal }) => {
  const { user } = useAuth();

  return (
    <header className="sticky top-0 z-20 bg-white/95 backdrop-blur-xs border-b border-slate-200">
      <div className="px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-4">
        {/* Left Section: Mobile Menu + Platform Tag */}
        <div className="flex items-center gap-3">
          <button
            onClick={onOpenSidebar}
            className="md:hidden p-2 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none"
          >
            <FiMenu className="h-5 w-5" />
          </button>
          
          <div className="flex items-center gap-2">
            <span className="hidden sm:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
              <FiCpu className="h-3.5 w-3.5 text-indigo-600" />
              <span>Groq Llama 3.3 Engine</span>
            </span>
          </div>
        </div>

        {/* Right Section: GitHub Status Pill + Role */}
        <div className="flex items-center gap-3">
          {user?.githubConnected ? (
            <div className="flex items-center gap-2 px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs">
              <FiGithub className="h-4 w-4 text-slate-700" />
              <span className="font-medium text-slate-800">@{user.githubUsername || 'connected'}</span>
              <span className="inline-block w-2 h-2 rounded-full bg-emerald-500"></span>
            </div>
          ) : (
            <button
              onClick={onOpenGithubModal}
              className="flex items-center gap-2 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-medium transition-colors shadow-xs cursor-pointer"
            >
              <FiGithub className="h-4 w-4" />
              <span>Connect GitHub</span>
            </button>
          )}

          <div className="hidden sm:block">
            <Badge variant="primary" size="sm">
              {user?.role || 'DEVELOPER'}
            </Badge>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
