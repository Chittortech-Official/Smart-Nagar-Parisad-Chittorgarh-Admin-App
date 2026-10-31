import { useState } from 'react';
import { Check, AlertTriangle, XCircle, Info, MessageSquare, ExternalLink } from 'lucide-react';

export type StatusModalType = 'confirm' | 'success' | 'warning' | 'error' | 'info';

export type WhatsAppAction = {
  label: string;
  phone: string;
  message: string;
  recipientName?: string;
  role?: 'CITIZEN' | 'COUNCILLOR' | 'OFFICER';
};

export type StatusModalConfig = {
  isOpen: boolean;
  type: StatusModalType;
  title: string;
  subtitle?: string;
  details?: string[];
  confirmText?: string;
  cancelText?: string;
  onConfirm?: () => void;
  onCancel?: () => void;
  showCancel?: boolean;
  whatsAppActions?: WhatsAppAction[];
};

// Runtime export safeguard for bundlers
export const StatusModalConfig = {};

interface StatusModalProps {
  config: StatusModalConfig;
  onClose: () => void;
}

import { cleanPhoneForWhatsApp } from '../lib/whatsapp';

export function StatusModal({ config, onClose }: StatusModalProps) {
  if (!config.isOpen) return null;

  const [recipientFilter, setRecipientFilter] = useState<'ALL' | 'OFFICER' | 'COUNCILLOR'>('ALL');

  const isConfirm = config.type === 'confirm';
  const isSuccess = config.type === 'success';
  const isWarning = config.type === 'warning';
  const isError = config.type === 'error';

  const hasForwardingOptions = config.whatsAppActions && config.whatsAppActions.some(wa => wa.role === 'COUNCILLOR' || wa.role === 'OFFICER');

  const displayedActions = (config.whatsAppActions || []).filter((wa) => {
    if (wa.role === 'CITIZEN' || !wa.role) return true;
    if (recipientFilter === 'ALL') return true;
    if (recipientFilter === 'OFFICER' && wa.role === 'OFFICER') return true;
    if (recipientFilter === 'COUNCILLOR' && wa.role === 'COUNCILLOR') return true;
    return false;
  });

  const handleOpenAll = () => {
    displayedActions.forEach((wa, idx) => {
      const cleanNum = cleanPhoneForWhatsApp(wa.phone);
      const waUrl = `https://wa.me/${cleanNum}?text=${encodeURIComponent(wa.message)}`;
      setTimeout(() => {
        window.open(waUrl, `_wa_chat_${idx}_${Date.now()}`);
      }, idx * 350);
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-in fade-in duration-200">
      <div 
        className="relative w-full max-w-md bg-white rounded-3xl shadow-2xl border border-slate-200 p-8 text-center space-y-4 animate-in zoom-in-95 duration-200 select-none max-h-[90vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Top Circular Icon (AMPLR Health Style) */}
        <div className="flex justify-center">
          {isSuccess && (
            <div className="w-20 h-20 rounded-full border-4 border-emerald-300 bg-emerald-50/60 flex items-center justify-center shadow-inner">
              <Check className="w-10 h-10 text-emerald-500 stroke-[3]" />
            </div>
          )}

          {isConfirm && (
            <div className="w-18 h-18 rounded-full border-4 border-amber-300 bg-amber-50 flex items-center justify-center shadow-inner">
              <span className="text-amber-500 font-bold text-3xl leading-none">!</span>
            </div>
          )}

          {isWarning && (
            <div className="w-18 h-18 rounded-full border-4 border-amber-400 bg-amber-50 flex items-center justify-center shadow-inner">
              <AlertTriangle className="w-10 h-10 text-amber-500 stroke-[2.2]" />
            </div>
          )}

          {isError && (
            <div className="w-18 h-18 rounded-full border-4 border-rose-400 bg-rose-50 flex items-center justify-center shadow-inner">
              <XCircle className="w-10 h-10 text-rose-500 stroke-[2.2]" />
            </div>
          )}

          {config.type === 'info' && (
            <div className="w-18 h-18 rounded-full border-4 border-blue-400 bg-blue-50 flex items-center justify-center shadow-inner">
              <Info className="w-10 h-10 text-blue-500 stroke-[2.2]" />
            </div>
          )}
        </div>

        {/* Title & Subtitle */}
        <div className="space-y-1.5">
          <h3 className="text-xl font-bold text-slate-900 tracking-tight">
            {config.title}
          </h3>
          {config.subtitle && (
            <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
              {config.subtitle}
            </p>
          )}
        </div>

        {/* Additional Details Box */}
        {config.details && config.details.length > 0 && (
          <div className="bg-slate-50 border border-slate-200 rounded-xl p-3.5 text-left space-y-1 text-xs text-slate-700 font-medium">
            {config.details.map((item, idx) => (
              <div key={idx} className="flex items-start gap-1.5">
                <span className="text-blue-600 font-bold">&bull;</span>
                <span>{item}</span>
              </div>
            ))}
          </div>
        )}

        {/* Direct WhatsApp Automated Actions Box */}
        {config.whatsAppActions && config.whatsAppActions.length > 0 && (
          <div className="bg-emerald-50/70 border border-emerald-200/90 rounded-2xl p-3.5 text-left space-y-2.5">
            <div className="flex items-center justify-between pb-1.5 border-b border-emerald-200/60">
              <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-900">
                <MessageSquare className="w-4 h-4 text-emerald-600" />
                <span>Pre-filled Official WhatsApp Messages:</span>
              </div>
              <button
                type="button"
                onClick={handleOpenAll}
                className="px-2.5 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-[10px] font-bold flex items-center gap-1 shadow-2xs transition-all active:scale-95 cursor-pointer"
                title="Opens WhatsApp Web chat for each recipient in a new tab"
              >
                <span>🚀 Open All Selected</span>
                <ExternalLink className="w-3 h-3" />
              </button>
            </div>

            {/* Recipient Filter Toggle (Citizen is always sent, customize municipal forward) */}
            {hasForwardingOptions && (
              <div className="p-2 bg-emerald-100/50 rounded-xl border border-emerald-200/70 flex flex-col sm:flex-row sm:items-center justify-between gap-1.5">
                <span className="text-[11px] font-bold text-emerald-950">
                  Select WhatsApp Forwarding:
                </span>
                <div className="flex items-center gap-1">
                  <button
                    type="button"
                    onClick={() => setRecipientFilter('ALL')}
                    className={`px-2.5 py-1 rounded-lg text-[10px] font-bold transition-all cursor-pointer ${
                      recipientFilter === 'ALL'
                        ? 'bg-emerald-700 text-white shadow-xs'
                        : 'bg-white/80 text-emerald-800 border border-emerald-200 hover:bg-white'
                    }`}
                  >
                    Both (पार्षद + अधिकारी)
                  </button>
                  <button
                    type="button"
                    onClick={() => setRecipientFilter('OFFICER')}
                    className={`px-2.5 py-1 rounded-lg text-[10px] font-bold transition-all cursor-pointer ${
                      recipientFilter === 'OFFICER'
                        ? 'bg-emerald-700 text-white shadow-xs'
                        : 'bg-white/80 text-emerald-800 border border-emerald-200 hover:bg-white'
                    }`}
                  >
                    Officer Only (केवल अधिकारी)
                  </button>
                  <button
                    type="button"
                    onClick={() => setRecipientFilter('COUNCILLOR')}
                    className={`px-2.5 py-1 rounded-lg text-[10px] font-bold transition-all cursor-pointer ${
                      recipientFilter === 'COUNCILLOR'
                        ? 'bg-emerald-700 text-white shadow-xs'
                        : 'bg-white/80 text-emerald-800 border border-emerald-200 hover:bg-white'
                    }`}
                  >
                    Councillor Only (केवल पार्षद)
                  </button>
                </div>
              </div>
            )}

            <div className="space-y-2">
              {displayedActions.map((wa, idx) => {
                const cleanNum = cleanPhoneForWhatsApp(wa.phone);
                const waUrl = `https://wa.me/${cleanNum}?text=${encodeURIComponent(wa.message)}`;
                return (
                  <div key={idx} className="p-2.5 rounded-xl bg-white border border-emerald-100 shadow-2xs flex items-center justify-between gap-3">
                    <div className="min-w-0">
                      <div className="text-xs font-bold text-slate-800 flex items-center gap-1.5 truncate">
                        <span>{wa.label}</span>
                        <span className="text-[10px] text-slate-400 font-mono">({wa.phone})</span>
                      </div>
                      <p className="text-[10px] text-slate-500 truncate mt-0.5">
                        {wa.message.replace(/\*/g, '').slice(0, 55)}...
                      </p>
                    </div>

                    <a
                      href={waUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs flex items-center gap-1.5 shrink-0 shadow-xs transition-transform active:scale-95 cursor-pointer"
                    >
                      <MessageSquare className="w-3.5 h-3.5" />
                      <span>Send Chat</span>
                      <ExternalLink className="w-3 h-3 opacity-80" />
                    </a>
                  </div>
                );
              })}
            </div>
            <p className="text-[10px] text-emerald-800/80 text-center pt-0.5">
              💡 Citizen confirmation is always included. Click "Send Chat" to open WhatsApp for whichever contact you wish to notify.
            </p>
          </div>
        )}

        {/* Actions Buttons */}
        <div className="flex items-center justify-center gap-3 pt-2">
          {/* Cancel button if confirmation */}
          {(isConfirm || config.showCancel) && (
            <button
              type="button"
              onClick={() => {
                if (config.onCancel) config.onCancel();
                onClose();
              }}
              className="px-5 py-2.5 rounded-xl border border-slate-300 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-xs transition-colors shadow-2xs cursor-pointer"
            >
              {config.cancelText || 'Cancel'}
            </button>
          )}

          {/* Primary confirm/OK button */}
          <button
            type="button"
            onClick={() => {
              if (config.onConfirm) {
                config.onConfirm();
              } else {
                onClose();
              }
            }}
            className={`px-8 py-2.5 rounded-xl font-bold text-sm text-white shadow-md transition-all active:scale-95 cursor-pointer ${
              isConfirm
                ? 'bg-teal-700 hover:bg-teal-800'
                : isSuccess
                ? 'bg-teal-700 hover:bg-teal-800'
                : isError
                ? 'bg-rose-600 hover:bg-rose-700'
                : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {config.confirmText || (isSuccess ? 'OK' : 'Yes, Confirm!')}
          </button>
        </div>
      </div>
    </div>
  );
}
