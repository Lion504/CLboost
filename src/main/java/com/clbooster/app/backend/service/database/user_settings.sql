-- User Settings Table
-- Stores user preferences and settings

CREATE TABLE IF NOT EXISTS user_settings (
    user_pin INT PRIMARY KEY,
    theme VARCHAR(20) DEFAULT 'system',
    language VARCHAR(50) DEFAULT 'English',
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT FALSE,
    product_updates BOOLEAN DEFAULT TRUE,
    marketing BOOLEAN DEFAULT FALSE,
    store_in_cloud BOOLEAN DEFAULT TRUE,
    allow_ai_training BOOLEAN DEFAULT FALSE,
    share_usage_data BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_pin) REFERENCES identification(Pin) ON DELETE CASCADE
);
