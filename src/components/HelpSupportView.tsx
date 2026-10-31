import React from 'react';
import { 
  Headphones, 
  Phone, 
  Mail, 
  MessageSquare, 
  ExternalLink, 
  Globe, 
  FileCode
} from 'lucide-react';

export const HelpSupportView: React.FC = () => {
  return (
    <div className="space-y-6">
      
      {/* 1. TOP CARD: 24/7 ChittorTech Developer Support Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xs p-6 relative overflow-hidden">
        {/* Subtle top accent gradient */}
        <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-emerald-500 via-sky-500 to-blue-600" />
        
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div className="space-y-2 max-w-2xl">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-600 flex items-center justify-center shrink-0">
                <Headphones className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900 tracking-tight">
                  24/7 ChittorTech Developer Support
                </h3>
                <span className="text-xs text-emerald-600 font-semibold flex items-center gap-1 mt-0.5">
                  <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                  24&times;7 चित्तौड़टेक आपकी सेवा के लिए सदैव तत्पर है
                </span>
              </div>
            </div>

            <p className="text-xs sm:text-sm text-slate-600 leading-relaxed pt-1">
              Need technical help, portal assistance, feature requests, or system troubleshooting? 
              Contact the official developer team anytime.
            </p>
          </div>

          {/* Visit ChittorTech button */}
          <div className="shrink-0">
            <a
              href="https://chittortech.in"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs shadow-md hover:shadow-lg transition-all group"
            >
              <Globe className="w-4 h-4 text-sky-400 group-hover:rotate-45 transition-transform" />
              <span>VISIT CHITTORTECH</span>
              <ExternalLink className="w-3.5 h-3.5 text-slate-400 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
            </a>
          </div>
        </div>

        {/* 2. FOUR INTERACTIVE ACTION CONTACT CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6 pt-6 border-t border-slate-100">
          
          {/* CARD 1: DIRECT PHONE CALL */}
          <a
            href="tel:+917597451057"
            className="group flex items-center gap-3.5 p-4 rounded-xl bg-emerald-50/60 hover:bg-emerald-50 border border-emerald-200/80 hover:border-emerald-300 transition-all shadow-2xs hover:shadow-sm"
          >
            <div className="w-11 h-11 rounded-xl bg-emerald-600 text-white flex items-center justify-center shrink-0 shadow-sm group-hover:scale-105 transition-transform">
              <Phone className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <p className="text-[10px] font-bold uppercase tracking-wider text-emerald-800">
                DIRECT PHONE CALL
              </p>
              <p className="text-sm font-bold text-slate-900 truncate group-hover:text-emerald-700 transition-colors">
                +91 7597451057
              </p>
            </div>
          </a>

          {/* CARD 2: WHATSAPP SUPPORT */}
          <a
            href="https://wa.me/917597451057?text=Hello%20ChittorTech%20Support,%20I%20am%20contacting%20you%20from%20Smart%20Nagar%20Parishad%20Chittorgarh%20Portal."
            target="_blank"
            rel="noopener noreferrer"
            className="group flex items-center gap-3.5 p-4 rounded-xl bg-emerald-50/60 hover:bg-emerald-50 border border-emerald-200/80 hover:border-emerald-300 transition-all shadow-2xs hover:shadow-sm"
          >
            <div className="w-11 h-11 rounded-xl bg-emerald-600 text-white flex items-center justify-center shrink-0 shadow-sm group-hover:scale-105 transition-transform">
              <MessageSquare className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <p className="text-[10px] font-bold uppercase tracking-wider text-emerald-800">
                WHATSAPP SUPPORT
              </p>
              <p className="text-sm font-bold text-slate-900 truncate group-hover:text-emerald-700 transition-colors">
                Chat on WhatsApp
              </p>
            </div>
          </a>

          {/* CARD 3: OFFICIAL SUPPORT EMAIL */}
          <a
            href="mailto:contact@chittortech.in"
            className="group flex items-center gap-3.5 p-4 rounded-xl bg-blue-50/60 hover:bg-blue-50 border border-blue-200/80 hover:border-blue-300 transition-all shadow-2xs hover:shadow-sm"
          >
            <div className="w-11 h-11 rounded-xl bg-blue-600 text-white flex items-center justify-center shrink-0 shadow-sm group-hover:scale-105 transition-transform">
              <Mail className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <p className="text-[10px] font-bold uppercase tracking-wider text-blue-800">
                OFFICIAL SUPPORT EMAIL
              </p>
              <p className="text-sm font-bold text-slate-900 truncate group-hover:text-blue-700 transition-colors">
                contact@chittortech.in
              </p>
            </div>
          </a>

          {/* CARD 4: DIRECT GMAIL */}
          <a
            href="mailto:business@chittortech.in"
            className="group flex items-center gap-3.5 p-4 rounded-xl bg-rose-50/60 hover:bg-rose-50 border border-rose-200/80 hover:border-rose-300 transition-all shadow-2xs hover:shadow-sm"
          >
            <div className="w-11 h-11 rounded-xl bg-rose-600 text-white flex items-center justify-center shrink-0 shadow-sm group-hover:scale-105 transition-transform">
              <Mail className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <p className="text-[10px] font-bold uppercase tracking-wider text-rose-800">
                DIRECT GMAIL
              </p>
              <p className="text-sm font-bold text-slate-900 truncate group-hover:text-rose-700 transition-colors">
                business@chittortech.in
              </p>
            </div>
          </a>

        </div>

        {/* Official Version Badge */}
        <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
          <div className="flex items-center gap-2">
            <FileCode className="w-4 h-4 text-slate-400" />
            <span>Official Portal Version:</span>
            <span className="font-mono font-bold text-slate-800 bg-slate-100 px-2 py-0.5 rounded">
              v2.4.0 (Enterprise Nagar Parishad Edition)
            </span>
          </div>
          <span className="text-slate-400 text-[11px]">
            ChittorTech Technical Support
          </span>
        </div>
      </div>

    </div>
  );
};
