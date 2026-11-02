import { useState, useEffect } from 'react';
import type { Ward } from '../types';
import { X, Save, UserCheck, Phone, MapPin, Building, Loader2 } from 'lucide-react';

interface EditParshadModalProps {
  isOpen: boolean;
  onClose: () => void;
  ward: Ward | null;
  onSave: (updatedWard: Ward) => Promise<void> | void;
}

export const EditParshadModal = ({
  isOpen,
  onClose,
  ward,
  onSave
}: EditParshadModalProps) => {
  const [formData, setFormData] = useState<Ward | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    if (ward) {
      setFormData({ ...ward });
      setIsSaving(false);
    }
  }, [ward, isOpen]);

  if (!isOpen || !ward || !formData) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.councillor_name.trim()) {
      alert('कृपया पार्षद का नाम दर्ज करें (Please enter Parshad Name)');
      return;
    }
    if (!formData.councillor_phone.trim()) {
      alert('कृपया पार्षद का मोबाइल नंबर दर्ज करें (Please enter Parshad Phone)');
      return;
    }

    setIsSaving(true);
    try {
      await onSave({
        ...formData,
        ward_number: ward.ward_number,
        ward_name: ward.ward_name,
        zone: ward.zone
      });
      setIsSaving(false);
      onClose();
    } catch (err: any) {
      setIsSaving(false);
      alert(err?.message || 'डेटाबेस में सहेजने में विफल। कृपया पुनः प्रयास करें।');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-in fade-in duration-200">
      <div 
        className="w-full max-w-lg bg-white rounded-3xl shadow-2xl border border-slate-200 overflow-hidden text-slate-800 animate-in zoom-in-95 duration-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div className="px-6 py-4 bg-gradient-to-r from-blue-700 to-indigo-800 text-white flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-white/10 text-white">
              <UserCheck className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-sm sm:text-base leading-tight">
                पार्षद विवरण संपादित करें (Edit Parshad)
              </h3>
              <p className="text-xs text-blue-100 font-mono">
                वार्ड {ward.ward_number} &bull; {ward.ward_name}
              </p>
            </div>
          </div>
          <button
            type="button"
            disabled={isSaving}
            onClick={onClose}
            className="p-1 rounded-lg text-white/80 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Ward Number (Fixed) */}
            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                वार्ड संख्या (Ward Number - Fixed)
              </label>
              <div className="px-3 py-2 rounded-xl bg-slate-100 border border-slate-200 font-mono font-bold text-slate-700 text-xs flex items-center justify-between select-none">
                <span>वार्ड {ward.ward_number}</span>
                <span className="text-[9px] uppercase font-bold text-slate-400 bg-white px-2 py-0.5 rounded border border-slate-200">Fixed</span>
              </div>
            </div>

            {/* Zone (Fixed) */}
            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                जोन (Zone - Fixed)
              </label>
              <div className="px-3 py-2 rounded-xl bg-slate-100 border border-slate-200 font-semibold text-slate-700 text-xs flex items-center justify-between select-none">
                <span>{ward.zone}</span>
                <span className="text-[9px] uppercase font-bold text-slate-400 bg-white px-2 py-0.5 rounded border border-slate-200">Fixed</span>
              </div>
            </div>
          </div>

          {/* Ward Name / Area (Fixed) */}
          <div className="space-y-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1">
              <Building className="w-3 h-3 text-slate-400" />
              <span>वार्ड का नाम / क्षेत्र (Ward Name - Fixed)</span>
            </label>
            <div className="px-3 py-2 rounded-xl bg-slate-100 border border-slate-200 font-semibold text-slate-800 text-xs flex items-center justify-between select-none">
              <span>{ward.ward_name}</span>
              <span className="text-[9px] uppercase font-bold text-slate-400 bg-white px-2 py-0.5 rounded border border-slate-200">Fixed</span>
            </div>
          </div>

          {/* Parshad Name (Editable) */}
          <div className="space-y-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1">
              <UserCheck className="w-3 h-3 text-slate-400" />
              <span>पार्षद का नाम (Elected Parshad Name)</span>
            </label>
            <input
              type="text"
              value={formData.councillor_name}
              onChange={(e) => setFormData({ ...formData, councillor_name: e.target.value })}
              required
              className="w-full px-3 py-2 rounded-xl border border-slate-300 text-xs text-slate-800 bg-white font-semibold focus:ring-2 focus:ring-blue-600 focus:outline-none"
              placeholder="e.g. Rajendra Bhati"
            />
          </div>

          {/* Parshad Phone (Editable) */}
          <div className="space-y-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1">
              <Phone className="w-3 h-3 text-slate-400" />
              <span>पार्षद मोबाइल नंबर (WhatsApp / Call Phone)</span>
            </label>
            <input
              type="tel"
              value={formData.councillor_phone}
              onChange={(e) => setFormData({ ...formData, councillor_phone: e.target.value })}
              required
              className="w-full px-3 py-2 rounded-xl border border-slate-300 text-xs text-slate-800 bg-white font-mono font-medium focus:ring-2 focus:ring-blue-600 focus:outline-none"
              placeholder="+919829000001"
            />
          </div>

          {/* Key Landmarks (Editable) */}
          <div className="space-y-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1">
              <MapPin className="w-3 h-3 text-slate-400" />
              <span>प्रमुख लैंडमार्क / मोहल्ले / पता (Key Landmarks)</span>
            </label>
            <textarea
              rows={2}
              value={formData.key_landmarks}
              onChange={(e) => setFormData({ ...formData, key_landmarks: e.target.value })}
              className="w-full px-3 py-2 rounded-xl border border-slate-300 text-xs text-slate-800 bg-white focus:ring-2 focus:ring-blue-600 focus:outline-none leading-relaxed"
              placeholder="e.g. Padan Pol, Bhairon Pol, Fort Foothills"
            />
          </div>

          {/* Actions */}
          <div className="pt-3 border-t border-slate-100 flex items-center justify-end gap-3">
            <button
              type="button"
              disabled={isSaving}
              onClick={onClose}
              className="px-4 py-2 rounded-xl border border-slate-200 text-xs font-semibold text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
            >
              रद्द करें (Cancel)
            </button>
            <button
              type="submit"
              disabled={isSaving}
              className="px-6 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 active:scale-95 text-white text-xs font-bold flex items-center gap-2 shadow-md transition-all cursor-pointer disabled:opacity-50"
            >
              {isSaving ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>सहेजा जा रहा है...</span>
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  <span>सहेजें (Save Changes)</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
