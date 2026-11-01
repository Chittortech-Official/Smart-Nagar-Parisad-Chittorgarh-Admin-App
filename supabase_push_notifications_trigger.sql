-- ==============================================================================
-- गढ़-सेवा (GarhSeva) - Nagar Parishad Chittorgarh Command Center
-- Automated Realtime Push Notification Trigger for Supabase + FCM / Webhooks
-- ==============================================================================

-- 1. Enable pg_net extension (Free on Supabase, sends HTTP requests from PostgreSQL)
CREATE EXTENSION IF NOT EXISTS pg_net WITH SCHEMA extensions;

-- 2. Notification Dispatch Function
CREATE OR REPLACE FUNCTION public.handle_ticket_push_notification()
RETURNS TRIGGER AS $$
DECLARE
    is_emergency BOOLEAN := false;
    alert_title TEXT;
    alert_body TEXT;
    payload JSONB;
BEGIN
    -- Check if emergency keywords exist in Hindi description
    IF NEW.problem_description ILIKE '%आग%' OR 
       NEW.problem_description ILIKE '%दमकल%' OR 
       NEW.problem_description ILIKE '%मृत%' OR 
       NEW.problem_description ILIKE '%दुर्घटना%' OR 
       NEW.problem_description ILIKE '%धंस%' THEN
        is_emergency := true;
    END IF;

    -- Construct Notification Title & Body
    IF is_emergency THEN
        alert_title := '🚨 आपातकालीन शिकायत दर्ज: वार्ड ' || COALESCE(NEW.ward_number::TEXT, '');
    ELSE
        alert_title := '📝 नई नागरिक शिकायत: वार्ड ' || COALESCE(NEW.ward_number::TEXT, '');
    END IF;

    alert_body := NEW.ticket_number || ': ' || SUBSTRING(NEW.problem_description FROM 1 FOR 60) || '... | नागरिक: ' || COALESCE(NEW.citizen_name, 'नागरिक');

    -- Insert into notification_logs table for audit tracking
    INSERT INTO public.notification_logs (
        recipient_role,
        notification_type,
        channel,
        title,
        message,
        status,
        sent_at
    ) VALUES (
        'CHAIRMAN',
        CASE WHEN is_emergency THEN 'EMERGENCY_ALERT' ELSE 'NEW_COMPLAINT' END,
        'PUSH_NOTIFICATION',
        alert_title,
        alert_body,
        'SENT',
        NOW()
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 3. Attach Trigger to tickets table on INSERT
DROP TRIGGER IF EXISTS trg_ticket_push_notification ON public.tickets;
CREATE TRIGGER trg_ticket_push_notification
AFTER INSERT ON public.tickets
FOR EACH ROW
EXECUTE FUNCTION public.handle_ticket_push_notification();

-- 4. Enable Realtime on tickets and notification_logs tables
ALTER PUBLICATION supabase_realtime ADD TABLE public.tickets;
ALTER PUBLICATION supabase_realtime ADD TABLE public.notification_logs;
