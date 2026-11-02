interface LogoProps {
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  theme?: 'dark' | 'light';
  hindiTitle?: string;
  englishSubtitle?: string;
  softwareBadge?: string;
}

export const NagarParishadLogo = ({ 
  className = '', 
  size = 'md', 
  theme = 'light',
  hindiTitle = 'नगर परिषद चित्तौड़गढ़',
  englishSubtitle = 'Nagar Parishad Chittorgarh',
  softwareBadge
}: LogoProps) => {

  const sizeMap = {
    sm: 'w-8 h-8',
    md: 'w-12 h-12',
    lg: 'w-16 h-16'
  };

  return (
    <div className={`flex items-center gap-2.5 ${className}`}>
      {/* Emblem SVG Icon */}
      <div className={`relative flex items-center justify-center rounded-xl bg-gradient-to-br from-amber-500 via-amber-600 to-amber-700 p-2 shadow-lg ring-2 ring-amber-400/30 ${sizeMap[size]} shrink-0`}>
        <svg 
          viewBox="0 0 100 100" 
          className="w-full h-full text-slate-950 fill-current"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Decorative outer circle */}
          <circle cx="50" cy="50" r="46" fill="none" stroke="#FFFFFF" strokeWidth="3" opacity="0.9" />
          <circle cx="50" cy="50" r="41" fill="none" stroke="#FFFFFF" strokeWidth="1" strokeDasharray="3 3" opacity="0.8" />
          
          {/* Sun / Rays Symbol (Rajasthan Pride) */}
          <path d="M50 12 L53 22 L63 15 L60 25 L71 22 L65 31 L77 32 L68 39 L78 44 L67 47 L75 55 L64 54 L68 64 L58 60 L58 71 L50 63 L42 71 L42 60 L32 64 L36 54 L25 55 L33 47 L22 44 L32 39 L23 32 L35 31 L29 22 L40 25 L37 15 L47 22 Z" fill="#FBBF24" opacity="0.3" />

          {/* Chittorgarh Vijay Stambh (Tower of Victory) Silhouette */}
          <polygon points="46,18 54,18 52,24 48,24" fill="#0B132B" />
          <polygon points="45,24 55,24 56,36 44,36" fill="#0B132B" />
          <rect x="42" y="36" width="16" height="4" rx="1" fill="#FFFFFF" />
          <polygon points="43,40 57,40 58,54 42,54" fill="#0B132B" />
          <rect x="40" y="54" width="20" height="4" rx="1" fill="#FFFFFF" />
          <polygon points="41,58 59,58 61,74 39,74" fill="#0B132B" />
          <rect x="36" y="74" width="28" height="5" rx="1" fill="#FFFFFF" />
          {/* Arch Base */}
          <path d="M34 79 L66 79 L68 88 L32 88 Z" fill="#0B132B" />
          <path d="M44 88 A 6 6 0 0 1 56 88 Z" fill="#FBBF24" />
        </svg>
      </div>

      {/* Typography */}
      <div className="flex flex-col text-left min-w-0">
        <span className={`font-bold tracking-tight leading-tight truncate ${theme === 'dark' ? 'text-white' : 'text-slate-900'} ${size === 'lg' ? 'text-xl' : size === 'md' ? 'text-base' : 'text-xs'}`}>
          {hindiTitle}
        </span>
        <span className={`text-[10px] font-semibold tracking-wider uppercase truncate ${theme === 'dark' ? 'text-amber-400' : 'text-amber-700'}`}>
          {englishSubtitle}
        </span>
        {softwareBadge && (
          <span className="text-[9px] font-mono font-medium text-sky-400 tracking-wide mt-0.5">
            {softwareBadge}
          </span>
        )}
      </div>
    </div>
  );
};
