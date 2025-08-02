-- Supabase Database Setup for InternLinkNG
-- Run this in your Supabase SQL Editor

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create hospitals table
CREATE TABLE hospitals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    state VARCHAR(100) NOT NULL,
    professions VARCHAR(255) NOT NULL, -- Comma-separated
    salary_range VARCHAR(100) NOT NULL,
    deadline VARCHAR(50) NOT NULL,
    created VARCHAR(50) DEFAULT '2024-07-28',
    online_application BOOLEAN NOT NULL DEFAULT false,
    application_url VARCHAR(255),
    physical_address VARCHAR(255) NOT NULL,
    profession_salaries TEXT, -- JSON string
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT false,
    phone_number VARCHAR(32),
    state_of_residence VARCHAR(64),
    profession VARCHAR(64),
    profile_picture TEXT,
    firstname VARCHAR(64),
    lastname VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX idx_hospitals_state ON hospitals(state);
CREATE INDEX idx_hospitals_online_application ON hospitals(online_application);
CREATE INDEX idx_users_email ON users(email);

-- Enable Row Level Security (RLS)
ALTER TABLE hospitals ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Create policies for hospitals (public read, admin write)
CREATE POLICY "Hospitals are viewable by everyone" ON hospitals
    FOR SELECT USING (true);

CREATE POLICY "Hospitals are insertable by authenticated users" ON hospitals
    FOR INSERT WITH CHECK (auth.role() = 'authenticated');

CREATE POLICY "Hospitals are updatable by admin" ON hospitals
    FOR UPDATE USING (auth.jwt() ->> 'is_admin' = 'true');

CREATE POLICY "Hospitals are deletable by admin" ON hospitals
    FOR DELETE USING (auth.jwt() ->> 'is_admin' = 'true');

-- Create policies for users
CREATE POLICY "Users can view their own data" ON users
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own data" ON users
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert their own data" ON users
    FOR INSERT WITH CHECK (auth.uid() = id);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_hospitals_updated_at BEFORE UPDATE ON hospitals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 