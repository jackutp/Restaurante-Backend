--
-- PostgreSQL database dump
--

\restrict CmEaTVSdilUL1NpDXqrdhjsPImquwVR6mXYE7MS4adhTq45OQTvjaY97KOWi75f

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-06-06 14:00:36 -05

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 17324)
-- Name: mesas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mesas (
    id bigint NOT NULL,
    numero integer NOT NULL,
    capacidad integer DEFAULT 4 NOT NULL,
    estado character varying(255) DEFAULT 'DISPONIBLE'::character varying NOT NULL,
    total_actual double precision DEFAULT 0.0 NOT NULL,
    orden_actual_id character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.mesas OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17341)
-- Name: mesas_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mesas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.mesas_id_seq OWNER TO postgres;

--
-- TOC entry 4478 (class 0 OID 0)
-- Dependencies: 220
-- Name: mesas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mesas_id_seq OWNED BY public.mesas.id;


--
-- TOC entry 4314 (class 2604 OID 17342)
-- Name: mesas id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mesas ALTER COLUMN id SET DEFAULT nextval('public.mesas_id_seq'::regclass);


--
-- TOC entry 4471 (class 0 OID 17324)
-- Dependencies: 219
-- Data for Name: mesas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mesas (id, numero, capacidad, estado, total_actual, orden_actual_id, created_at, updated_at) FROM stdin;
12	2	4	DISPONIBLE	0	\N	2026-05-20 17:17:39.285108	2026-05-20 17:17:39.285108
11	1	4	OCUPADO	44	\N	2026-05-20 17:17:36.24415	2026-05-20 17:32:24.556926
13	3	4	OCUPADO	44	\N	2026-05-20 17:17:42.524621	2026-05-20 17:32:39.405554
\.


--
-- TOC entry 4479 (class 0 OID 0)
-- Dependencies: 220
-- Name: mesas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mesas_id_seq', 180, true);


--
-- TOC entry 4321 (class 2606 OID 17344)
-- Name: mesas mesas_numero_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mesas
    ADD CONSTRAINT mesas_numero_key UNIQUE (numero);


--
-- TOC entry 4323 (class 2606 OID 17346)
-- Name: mesas mesas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mesas
    ADD CONSTRAINT mesas_pkey PRIMARY KEY (id);


-- Completed on 2026-06-06 14:00:36 -05

--
-- PostgreSQL database dump complete
--

\unrestrict CmEaTVSdilUL1NpDXqrdhjsPImquwVR6mXYE7MS4adhTq45OQTvjaY97KOWi75f

