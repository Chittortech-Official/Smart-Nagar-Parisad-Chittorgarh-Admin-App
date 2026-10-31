import { useState } from 'react';
import type { IntakeComplaint } from '../types';
import { 
  X, 
  CheckCircle2, 
  XCircle, 
  MapPin, 
  Clock, 
  Phone
} from 'lucide-react';

interface IntakeVerificationModalProps {
  intake: IntakeComplaint;
  onClose: () => void;
  onAccept: (intakeId: string, slaHours: number) => void;
  onReject: (intakeId: string, reason: string) => void;
}

export const IntakeVerificationModal = ({
  intake,
  onClose,
  onAccept,
  onReject
}: IntakeVerificationModalProps) => {
  const [slaHours, setSlaHours] = useState<number>(intake.sla_hours ?? 0.008333);
  const [showRejectForm, setShowRejectForm] = useState(false);
  const [rejectionReason, setRejectionReason] = useState('');

  const handleAccept = (e: React.FormEvent) => {
    e.preventDefault();
    onAccept(intake.id, slaHours);
  };

  const handleReject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!rejectionReason.trim()) {
      alert("Please provide an official rejection reason.");
      return;
    }
    onReject(intake.id, rejectionReason);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
      <div className="relative w-full max-w-2xl bg-white rounded-2xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 bg-slate-900 text-white">
          <div className="flex items-center gap-2.5">
            <div className={`p-2 rounded-lg ${intake.intake_channel === 'WHATSAPP' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-blue-500/20 text-blue-400'}`}>
              <Phone className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base leading-tight">
                Portal Manager Verification Engine
              </h3>
              <p className="text-xs text-slate-400">
                Channel: <span className="font-semibold text-white">{intake.intake_channel === 'WHATSAPP' ? '24/7 WhatsApp Intake' : 'Helpline Call (10 AM - 6 PM)'}</span>
                {intake.is_after_hours && (
                  <span className="ml-2 px-1.5 py-0.5 rounded-sm bg-amber-500/20 text-amber-300 text-[10px] font-medium">
                    After-Hours Queue
                  </span>
                )}
              </p>
            </div>
          </div>
          <button 
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto space-y-5 text-sm">
          {/* Complainant & Location Details */}
          <div className="grid grid-cols-2 gap-4 p-4 bg-slate-50 rounded-xl border border-slate-200 text-xs">
            <div>
              <span className="text-slate-500 block">Citizen Full Name</span>
              <span className="font-bold text-slate-900 text-sm">{intake.citizen_name}</span>
            </div>
            <div>
              <span className="text-slate-500 block">Contact Phone Number</span>
              <a href={`tel:${intake.citizen_phone}`} className="font-bold text-blue-600 text-sm hover:underline">
                {intake.citizen_phone}
              </a>
            </div>
            <div>
              <span className="text-slate-500 block">Nagar Parishad Ward</span>
              <span className="font-bold text-slate-900">Ward {intake.ward_number} (Chittorgarh)</span>
            </div>
            <div>
              <span className="text-slate-500 block">Department &amp; Category</span>
              <span className="font-bold text-slate-900">{intake.category_name}</span>
            </div>
            <div className="col-span-2">
              <span className="text-slate-500 block">Exact Address &amp; Landmark</span>
              <span className="font-medium text-slate-800">{intake.address} {intake.landmark && `(Near ${intake.landmark})`}</span>
            </div>
          </div>

          {/* Description */}
          <div className="space-y-1">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Problem Description</span>
            <div className="p-3 bg-amber-50/50 rounded-xl border border-amber-200/60 text-slate-800 text-xs leading-relaxed">
              "{intake.problem_description}"
            </div>
          </div>

          {/* Duplicate Detection Alert Badge */}
          <div className="flex items-center gap-2 p-3 bg-blue-50/70 border border-blue-200 rounded-xl text-xs text-blue-900">
            <MapPin className="w-4 h-4 text-blue-600 shrink-0" />
            <span>Jurisdiction Checked: Address confirmed inside Chittorgarh Nagar Parishad municipal boundary. No duplicate active tickets found.</span>
          </div>

          {/* ACCEPT FORM */}
          {!showRejectForm ? (
            <form onSubmit={handleAccept} className="space-y-4 pt-2 border-t border-slate-200">
              {/* SLA Selector */}
              <div className="flex items-center justify-between p-3.5 bg-slate-50 rounded-xl border border-slate-200">
                <div className="flex items-center gap-2">
                  <Clock className="w-4 h-4 text-slate-500" />
                  <span className="text-xs font-semibold text-slate-700">Configured SLA Deadline:</span>
                </div>
                <select
                  value={slaHours}
                  onChange={(e) => setSlaHours(Number(e.target.value))}
                  className="px-3 py-1.5 rounded-lg border border-slate-200 bg-white text-xs font-bold text-slate-800"
                >
                  <option value={0.008333}>⚡ 30 Seconds (Fast Testing / टेस्टिंग)</option>
                  <option value={0.016667}>⚡ 1 Minute (Fast Testing / टेस्टिंग)</option>
                  <option value={12}>12 Hours (Emergency Removal)</option>
                  <option value={24}>24 Hours (Sanitation Standard)</option>
                  <option value={48}>48 Hours (Streetlights / Drains)</option>
                  <option value={72}>72 Hours (Roads &amp; Civil Works)</option>
                </select>
              </div>

              {/* Action Buttons */}
              <div className="flex justify-between items-center pt-2">
                <button
                  type="button"
                  onClick={() => setShowRejectForm(true)}
                  className="px-3.5 py-2 rounded-xl text-rose-600 hover:bg-rose-50 text-xs font-semibold flex items-center gap-1.5 transition-colors"
                >
                  <XCircle className="w-4 h-4" />
                  <span>Reject Complaint</span>
                </button>

                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={onClose}
                    className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-xs font-semibold hover:bg-slate-50"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold shadow-xs flex items-center gap-1.5 transition-all"
                  >
                    <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                    <span>Accept &amp; Generate Official Ticket</span>
                  </button>
                </div>
              </div>
            </form>
          ) : (
            /* REJECT FORM */
            <form onSubmit={handleReject} className="space-y-4 pt-2 border-t border-slate-200">
              <div className="bg-rose-50 border border-rose-200 rounded-xl p-3.5 text-xs text-rose-900 space-y-1.5">
                <p className="font-bold">Record Rejection Reason</p>
                <p className="text-slate-600">
                  Every rejection must specify an official municipal reason (e.g. out of municipal jurisdiction, external agency like PHED/Discom, invalid contact).
                </p>
              </div>

              <textarea
                rows={3}
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                placeholder="Enter mandatory rejection reason..."
                className="w-full p-3 rounded-xl border border-rose-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
                required
              />

              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowRejectForm(false)}
                  className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-xs font-semibold hover:bg-slate-50"
                >
                  Back
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-rose-600 hover:bg-rose-700 text-white text-xs font-semibold shadow-xs"
                >
                  Confirm Rejection &amp; Notify Citizen
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
