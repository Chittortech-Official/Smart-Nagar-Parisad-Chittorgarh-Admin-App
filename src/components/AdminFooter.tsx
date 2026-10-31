import React from 'react';
import { ExternalLink, Phone, MapPin, Award, ShieldCheck } from 'lucide-react';

export const AdminFooter: React.FC = () => {
  return (
    <footer className="mt-6 rounded-2xl border border-sky-900/40 bg-gradient-to-b from-slate-900 via-[#0B1728] to-[#070F1D] text-white shadow-xl overflow-hidden relative">
      {/* Subtle top accent gradient line */}
      <div className="h-0.5 w-full bg-gradient-to-r from-transparent via-sky-500/60 to-transparent" />

      <div className="px-6 py-4 space-y-3.5 text-center flex flex-col items-center">
        
        {/* ROW 1: Centered Municipal Brand & Identity */}
        <div className="w-full flex flex-col items-center justify-center gap-2 pb-3 border-b border-slate-800/80">
          <div className="flex flex-wrap items-center justify-center gap-2.5">
            {/* Vijay Stambh Emblem Icon */}
            <div className="relative flex items-center justify-center rounded-xl bg-gradient-to-br from-amber-500 via-amber-600 to-amber-700 p-1.5 shadow-md ring-1 ring-amber-400/40 w-7 h-7 shrink-0">
              <svg 
                viewBox="0 0 100 100" 
                className="w-full h-full text-slate-950 fill-current"
                xmlns="http://www.w3.org/2000/svg"
              >
                <circle cx="50" cy="50" r="46" fill="none" stroke="#FFFFFF" strokeWidth="3" opacity="0.9" />
                <circle cx="50" cy="50" r="41" fill="none" stroke="#FFFFFF" strokeWidth="1" strokeDasharray="3 3" opacity="0.8" />
                <path d="M50 12 L53 22 L63 15 L60 25 L71 22 L65 31 L77 32 L68 39 L78 44 L67 47 L75 55 L64 54 L68 64 L58 60 L58 71 L50 63 L42 71 L42 60 L32 64 L36 54 L25 55 L33 47 L22 44 L32 39 L23 32 L35 31 L29 22 L40 25 L37 15 L47 22 Z" fill="#FBBF24" opacity="0.3" />
                <polygon points="46,18 54,18 52,24 48,24" fill="#0B132B" />
                <polygon points="45,24 55,24 56,36 44,36" fill="#0B132B" />
                <rect x="42" y="36" width="16" height="4" rx="1" fill="#FFFFFF" />
                <polygon points="43,40 57,40 58,54 42,54" fill="#0B132B" />
                <rect x="40" y="54" width="20" height="4" rx="1" fill="#FFFFFF" />
                <polygon points="41,58 59,58 61,74 39,74" fill="#0B132B" />
                <rect x="36" y="74" width="28" height="5" rx="1" fill="#FFFFFF" />
                <path d="M34 79 L66 79 L68 88 L32 88 Z" fill="#0B132B" />
                <path d="M44 88 A 6 6 0 0 1 56 88 Z" fill="#FBBF24" />
              </svg>
            </div>

            <h4 className="font-bold text-white text-sm sm:text-base tracking-tight">
              नगर परिषद पोर्टल (Nagar Parishad Portal)
            </h4>
            <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-sky-950/80 border border-sky-800/60 text-sky-300">
              चित्तौड़गढ़ (राज.)
            </span>
          </div>

          <p className="text-[11px] text-slate-400 text-center">
            स्वायत्त शासन विभाग, राजस्थान सरकार &bull; Smart Nagar Parishad Chittorgarh Live Portal
          </p>
        </div>

        {/* ROW 2: Centered Civic Leadership (Sabhapati & Upsabhapati) */}
        <div className="w-full flex flex-wrap items-center justify-center gap-3 py-0.5">
          <div className="flex items-center gap-1.5 text-xs text-slate-400 font-medium">
            <Award className="w-3.5 h-3.5 text-amber-400 shrink-0" />
            <span>नगर परिषद नेतृत्व:</span>
          </div>

          {/* Sabhapati Pill */}
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-lg bg-slate-800/80 border border-amber-500/30 text-xs shadow-xs">
            <span className="text-[10px] uppercase font-bold tracking-wider text-amber-400">सभापति</span>
            <span className="text-white font-semibold">श्री अनिल ईनाणी</span>
            <span className="text-slate-400 text-[10px]">(Anil Inani)</span>
          </div>

          <span className="text-slate-700 hidden sm:inline">&bull;</span>

          {/* Upsabhapati Pill */}
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-lg bg-slate-800/80 border border-sky-500/30 text-xs shadow-xs">
            <span className="text-[10px] uppercase font-bold tracking-wider text-sky-400">उपसभापति</span>
            <span className="text-white font-semibold">श्री सुदर्शन रामपुरिया</span>
            <span className="text-slate-400 text-[10px]">(Sudarshan Rampuria)</span>
          </div>
        </div>

        {/* ROW 3: Centered Office Address & Helpline */}
        <div className="w-full flex flex-wrap items-center justify-center gap-x-6 gap-y-2 pt-2 border-t border-slate-800/60 text-xs text-slate-300">
          <div className="flex items-center justify-center gap-1.5 text-slate-300">
            <MapPin className="w-3.5 h-3.5 text-amber-400 shrink-0" />
            <span>कार्यालय: नगर परिषद, स्टेशन रोड/किला मार्ग, चित्तौड़गढ़ (राज.)</span>
          </div>
          <span className="text-slate-700 hidden sm:inline">&bull;</span>
          <div className="flex items-center justify-center gap-1.5 text-slate-300">
            <Phone className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
            <span>कंट्रोल रूम हेल्पलाइन: <strong className="text-white font-semibold">181</strong> / 01472-241112</span>
          </div>
          <span className="text-slate-700 hidden md:inline">&bull;</span>
          <div className="flex items-center justify-center gap-1.5 text-[11px] text-slate-400">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            <span>256-Bit SSL Encrypted</span>
          </div>
        </div>

        {/* ROW 4: Centered Tech Credits & Copyright */}
        <div className="w-full pt-2.5 border-t border-slate-800/80 flex flex-col items-center justify-center gap-1.5 text-center">
          <div className="inline-flex items-center justify-center gap-1.5 text-xs text-slate-300">
            <span className="text-slate-400">Designed &amp; Developed by</span>
            <a 
              href="https://chittortech.in" 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-sky-400 hover:text-sky-300 font-bold tracking-wide hover:underline inline-flex items-center gap-1 transition-colors"
            >
              ChittorTech
              <ExternalLink className="w-3 h-3 inline" />
            </a>
          </div>

          <div className="text-[11px] text-slate-400">
            &copy; 2026 <strong className="text-slate-300">नगर परिषद चित्तौड़गढ़ (Nagar Parishad Chittorgarh)</strong>. सर्वाधिकार सुरक्षित (All Rights Reserved).
          </div>
        </div>

      </div>
    </footer>
  );
};
