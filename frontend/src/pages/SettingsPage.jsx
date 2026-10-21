import React, { useState } from 'react';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Select from '../components/common/Select';
import { toast } from 'react-toastify';
import { FiSettings, FiCpu, FiShield, FiSave, FiInfo } from 'react-icons/fi';

export const SettingsPage = () => {
  const [model, setModel] = useState('llama-3.3-70b-versatile');
  const [minScoreThreshold, setMinScoreThreshold] = useState('7');
  const [autoApprovePassing, setAutoApprovePassing] = useState(false);
  const [strictSecurity, setStrictSecurity] = useState(true);

  const handleSave = (e) => {
    e.preventDefault();
    toast.success('Workspace settings updated successfully');
  };

  return (
    <div className="space-y-6 max-w-4xl">
      {/* Header */}
      <div className="pb-2 border-b border-slate-200">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Platform Settings</h1>
        <p className="text-sm text-slate-500 mt-1">
          Configure AI inspection models, security thresholds, and automated review policies.
        </p>
      </div>

      <form onSubmit={handleSave} className="space-y-6">
        {/* AI Model Configuration */}
        <Card title="AI Review Engine" subtitle="Configured via Groq Cloud LLM provider">
          <div className="space-y-4">
            <div className="p-3 bg-indigo-50/70 border border-indigo-100 rounded-lg flex items-start gap-2.5 text-xs text-indigo-900">
              <FiInfo className="h-4 w-4 shrink-0 text-indigo-600 mt-0.5" />
              <span>
                Groq API accelerates inference at up to 500 tokens/sec. The backend calls Groq directly via Spring Boot RestClient with zero secret exposure to client browsers.
              </span>
            </div>

            <Select
              label="Selected AI Model"
              id="model"
              value={model}
              onChange={(e) => setModel(e.target.value)}
              options={[
                { value: 'llama-3.3-70b-versatile', label: 'Llama 3.3 70B Versatile (Recommended - High reasoning)' },
                { value: 'llama-3.1-8b-instant', label: 'Llama 3.1 8B Instant (Ultra low latency)' },
                { value: 'mixtral-8x7b-32768', label: 'Mixtral 8x7B (High context window)' },
              ]}
              helperText="Managed globally through root .env (GROQ_MODEL)"
            />

            <div className="pt-2">
              <Select
                label="Passing Code Quality Score Threshold (1-10)"
                id="threshold"
                value={minScoreThreshold}
                onChange={(e) => setMinScoreThreshold(e.target.value)}
                options={[
                  { value: '8', label: 'Score 8+ (Strict enterprise bar)' },
                  { value: '7', label: 'Score 7+ (Standard recommendation)' },
                  { value: '6', label: 'Score 6+ (Permissive)' },
                ]}
              />
            </div>
          </div>
        </Card>

        {/* Security & Policies */}
        <Card title="Security & Review Rules" subtitle="Automated triage and warning thresholds">
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 bg-slate-50 border border-slate-200 rounded-lg">
              <div>
                <p className="text-sm font-semibold text-slate-800">Block PR on CRITICAL findings</p>
                <p className="text-xs text-slate-500">Flag PR as requiring manual developer fix if critical vulnerability is detected.</p>
              </div>
              <input
                type="checkbox"
                checked={strictSecurity}
                onChange={(e) => setStrictSecurity(e.target.checked)}
                className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer"
              />
            </div>

            <div className="flex items-center justify-between p-3 bg-slate-50 border border-slate-200 rounded-lg">
              <div>
                <p className="text-sm font-semibold text-slate-800">Auto-tag passing PRs</p>
                <p className="text-xs text-slate-500">Mark PR as verified when code score meets or exceeds quality threshold.</p>
              </div>
              <input
                type="checkbox"
                checked={autoApprovePassing}
                onChange={(e) => setAutoApprovePassing(e.target.checked)}
                className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer"
              />
            </div>
          </div>
        </Card>

        <div className="flex justify-end">
          <Button type="submit" variant="primary" size="md" icon={FiSave}>
            Save Preferences
          </Button>
        </div>
      </form>
    </div>
  );
};

export default SettingsPage;
