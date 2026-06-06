--
-- PostgreSQL database dump
--

\restrict jdojzpSqWCdgies0rwvKDW3BNiSI1TlEfJktgMjJpKa4HfneGcphfRaxNYVlbeZ

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-06-06 13:59:59 -05

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
-- TOC entry 219 (class 1259 OID 17152)
-- Name: mermas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mermas (
    mermaid integer NOT NULL,
    tipo_merma character varying(255) NOT NULL,
    nombre_merma character varying(255) NOT NULL,
    cantidad character varying(255) NOT NULL,
    motivo character varying(500) NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    referencia_id integer,
    unidad_medida character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tipo CHECK (((tipo_merma)::text = ANY (ARRAY[('PRODUCTO'::character varying)::text, ('INSUMO'::character varying)::text])))
);


ALTER TABLE public.mermas OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17167)
-- Name: mermas_mermaid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mermas_mermaid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.mermas_mermaid_seq OWNER TO postgres;

--
-- TOC entry 4475 (class 0 OID 0)
-- Dependencies: 220
-- Name: mermas_mermaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mermas_mermaid_seq OWNED BY public.mermas.mermaid;


--
-- TOC entry 4314 (class 2604 OID 17168)
-- Name: mermas mermaid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mermas ALTER COLUMN mermaid SET DEFAULT nextval('public.mermas_mermaid_seq'::regclass);


--
-- TOC entry 4468 (class 0 OID 17152)
-- Dependencies: 219
-- Data for Name: mermas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mermas (mermaid, tipo_merma, nombre_merma, cantidad, motivo, fecha, referencia_id, unidad_medida, created_at, updated_at) FROM stdin;
2	PRODUCTO	Filet Mignon	2 porciones	Cliente canceló pedido	2026-05-08 20:30:00	\N	\N	2026-05-09 12:51:25.326199	2026-05-09 12:51:25.326199
3	INSUMO	Trufa Negra	0.5	Producto en mal estado	2026-05-07 10:00:00	\N	\N	2026-05-09 12:51:25.326199	2026-05-09 12:51:25.326199
5	PRODUCTO	gato pat	1 porciones	El mesero no pudo mas	2026-05-09 13:09:32.591837	10		2026-05-09 13:09:32.598097	2026-05-09 13:09:32.598097
\.


--
-- TOC entry 4476 (class 0 OID 0)
-- Dependencies: 220
-- Name: mermas_mermaid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mermas_mermaid_seq', 112, true);


--
-- TOC entry 4320 (class 2606 OID 17170)
-- Name: mermas mermas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mermas
    ADD CONSTRAINT mermas_pkey PRIMARY KEY (mermaid);


-- Completed on 2026-06-06 14:00:00 -05

--
-- PostgreSQL database dump complete
--

\unrestrict jdojzpSqWCdgies0rwvKDW3BNiSI1TlEfJktgMjJpKa4HfneGcphfRaxNYVlbeZ

