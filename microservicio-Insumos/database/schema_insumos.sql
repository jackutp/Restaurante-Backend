--
-- PostgreSQL database dump
--

\restrict h116eUxQ4wxAGj7XVZjYvModpBQwPIJSf6XfyU3azMQ9AyNJfzpvJaQ4cUzUUpx

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-06-06 13:59:38 -05

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
-- TOC entry 219 (class 1259 OID 17132)
-- Name: insumos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.insumos (
    insumoid integer NOT NULL,
    nombre character varying(100) NOT NULL,
    unidad_medida character varying(255) NOT NULL,
    stock integer DEFAULT 0 NOT NULL,
    estado_insumo character varying(255) DEFAULT 'VACIO'::character varying NOT NULL,
    CONSTRAINT chk_estado CHECK (((estado_insumo)::text = ANY (ARRAY[('DISPONIBLE'::character varying)::text, ('BAJO'::character varying)::text, ('VACIO'::character varying)::text]))),
    CONSTRAINT chk_unidad CHECK (((unidad_medida)::text = ANY (ARRAY[('KG'::character varying)::text, ('LATAS'::character varying)::text, ('G'::character varying)::text, ('L'::character varying)::text, ('ML'::character varying)::text])))
);


ALTER TABLE public.insumos OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17146)
-- Name: insumos_insumoid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.insumos_insumoid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.insumos_insumoid_seq OWNER TO postgres;

--
-- TOC entry 4477 (class 0 OID 0)
-- Dependencies: 220
-- Name: insumos_insumoid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.insumos_insumoid_seq OWNED BY public.insumos.insumoid;


--
-- TOC entry 4314 (class 2604 OID 17147)
-- Name: insumos insumoid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.insumos ALTER COLUMN insumoid SET DEFAULT nextval('public.insumos_insumoid_seq'::regclass);


--
-- TOC entry 4470 (class 0 OID 17132)
-- Dependencies: 219
-- Data for Name: insumos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.insumos (insumoid, nombre, unidad_medida, stock, estado_insumo) FROM stdin;
3	Filete de Res	KG	50	DISPONIBLE
9	Harina	KG	50	DISPONIBLE
10	Aceite de Oliva	L	50	DISPONIBLE
11	Insumo Genérico	L	50	DISPONIBLE
12	Insumo Genérico 2	L	50	DISPONIBLE
\.


--
-- TOC entry 4478 (class 0 OID 0)
-- Dependencies: 220
-- Name: insumos_insumoid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.insumos_insumoid_seq', 66, true);


--
-- TOC entry 4320 (class 2606 OID 17149)
-- Name: insumos insumos_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.insumos
    ADD CONSTRAINT insumos_nombre_key UNIQUE (nombre);


--
-- TOC entry 4322 (class 2606 OID 17151)
-- Name: insumos insumos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.insumos
    ADD CONSTRAINT insumos_pkey PRIMARY KEY (insumoid);


-- Completed on 2026-06-06 13:59:39 -05

--
-- PostgreSQL database dump complete
--

\unrestrict h116eUxQ4wxAGj7XVZjYvModpBQwPIJSf6XfyU3azMQ9AyNJfzpvJaQ4cUzUUpx

