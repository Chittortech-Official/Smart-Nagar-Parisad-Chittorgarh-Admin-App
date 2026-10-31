import { useState, useEffect, type ReactNode } from 'react';
import { ShieldAlert, Laptop, Monitor, AlertTriangle, Phone, RefreshCw } from 'lucide-react';
import { NagarParishadLogo } from './NagarParishadLogo';

interface SecurityDeviceGuardProps {
  children: ReactNode;
}

export function SecurityDeviceGuard({ children }: SecurityDeviceGuardProps) {
  const [windowWidth, setWindowWidth] = useState<number>(
    typeof window !== 'undefined' ? window.innerWidth : 1280
  );

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const MIN_LAPTOP_WIDTH = 1024; // Smallest standard laptop screen width (e.g. 1024x768 / 11-13" laptops)
  const isBelowLaptop = windowWidth < MIN_LAPTOP_WIDTH;

  if (isBelowLaptop) {
    return (
      <div className="min-h-screen w-full bg-slate-900 text-slate-100 flex items-center justify-center p-6 antialiased select-none">
        <div className="max-w-md w-full bg-slate-800/90 border border-slate-700/80 rounded-2xl shadow-2xl p-6 sm:p-8 text-center space-y-6 backdrop-blur-md">
          {/* Official Emblem & Branding */}
          <div className="flex justify-center">
            <NagarParishadLogo size="md" theme="dark" />
          </div>

          {/* Security Alert Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-rose-500/20 border border-rose-500/40 text-rose-300 text-xs font-semibold">
            <ShieldAlert className="w-4 h-4 text-rose-400" />
            <span>सुरक्षा प्रतिबंध &bull; अधिकृत वर्कस्टेशन अनिवार्य</span>
          </div>

          {/* Title & Primary Notice */}
          <div className="space-y-2">
            <h1 className="text-xl sm:text-2xl font-bold text-white tracking-tight">
              एडमिन पोर्टल केवल लैपटॉप / डेस्कटॉप पर उपलब्ध है
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
              नगर परिषद चित्तौड़गढ़ के डेटा सुरक्षा, नागरिक गोपनीयता एवं प्रशासनिक प्रोटोकॉल के तहत यह पोर्टल मोबाइल डिवाइस या संकीर्ण स्क्रीन पर प्रतिबंधित है।
            </p>
          </div>

          {/* Device Detection Info Box */}
          <div className="bg-slate-900/80 rounded-xl p-4 border border-slate-700 text-left space-y-2.5 text-xs">
            <div className="flex items-center justify-between text-slate-400 pb-2 border-b border-slate-800">
              <span className="flex items-center gap-1.5 font-medium">
                <AlertTriangle className="w-3.5 h-3.5 text-amber-400" />
                <span>पहचाना गया डिस्प्ले:</span>
              </span>
              <span className="font-mono text-amber-300 font-bold">{windowWidth}px चौड़ाई</span>
            </div>
            <div className="flex items-center justify-between text-slate-400">
              <span className="flex items-center gap-1.5 font-medium">
                <Laptop className="w-3.5 h-3.5 text-blue-400" />
                <span>न्यूनतम आवश्यक लैपटॉप स्क्रीन:</span>
              </span>
              <span className="font-mono text-emerald-400 font-bold">{MIN_LAPTOP_WIDTH}px चौड़ाई</span>
            </div>
          </div>

          {/* Instructions */}
          <div className="space-y-2 text-xs text-slate-300 bg-blue-950/40 border border-blue-900/50 rounded-xl p-3.5 text-left">
            <div className="flex items-center gap-2 text-blue-400 font-bold">
              <Monitor className="w-4 h-4 shrink-0" />
              <span>पोर्टल खोलने के निर्देश:</span>
            </div>
            <ul className="list-disc list-inside space-y-1 text-slate-300 pl-1">
              <li>कृपया अपने लैपटॉप या डेस्कटॉप कंप्यूटर में लॉगिन करें।</li>
              <li>यदि आप लैपटॉप पर हैं, तो ब्राउज़र विंडो को अधिकतम (Maximize) करें।</li>
            </ul>
          </div>

          {/* Technical Support & Action */}
          <div className="pt-2 border-t border-slate-700/60 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs">
            <a
              href="tel:+917597451057"
              className="inline-flex items-center gap-1.5 text-blue-400 hover:text-blue-300 font-medium"
            >
              <Phone className="w-3.5 h-3.5" />
              <span>चित्तौड़टेक सहायता: +91 7597451057</span>
            </a>

            <button
              type="button"
              onClick={() => setWindowWidth(window.innerWidth)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-700 hover:bg-slate-600 text-white font-medium text-xs transition-colors"
            >
              <RefreshCw className="w-3 h-3" />
              <span>स्क्रीन पुनः जाँचें</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}
